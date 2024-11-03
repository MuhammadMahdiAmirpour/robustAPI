package com.github.robustapi.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    private volatile boolean running = true;
    private Thread monitoringThread;
    private SparkSession spark;

    @Autowired
    public MonitoringService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        createRequiredTables();
        startMonitoring();
    }

    public void startMonitoring() {
        try {
            monitoringThread = new Thread(this::monitoringLoop, "monitoring-thread");
            monitoringThread.start();
            logger.info("Monitoring thread started");
        } catch (Exception e) {
            logger.error("Failed to start monitoring service: {}", e.getMessage(), e);
        }
    }

    private synchronized void ensureSparkInitialized() {
        if (spark == null) {
            initializeSparkSession();
        }
    }

    private void initializeSparkSession() {
        try {
            SparkSession.Builder builder = SparkSession
                    .builder()
                    .appName("MonitoringTool")
                    .master("local[*]")
                    .config("spark.driver.userClassPathFirst", "true")
                    .config("spark.executor.userClassPathFirst", "true")
                    .config("spark.ui.enabled", false)
                    .config("spark.driver.host", "localhost")
                    .config("spark.driver" + ".bindAddress", "0.0.0.0")
                    .config("spark" + ".executor.memory", "1g")
                    .config("spark" + ".driver" + ".memory", "1g")
                    .config("spark.sql" + ".warehouse.dir", "/tmp/spark" + "-warehouse")
                    // Add these configurations to resolve Java 9+ compatibility
                    // issues.
                    .config("spark.driver.extraJavaOptions",
                            "--add-opens=java.base/java.lang=ALL-UNNAMED "
                                    + "--add-opens=java.base/java.nio=ALL-UNNAMED "
                                    + "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED")
                    .config("spark.executor.extraJavaOptions",
                            "--add-opens=java.base/java.lang=ALL-UNNAMED "
                                    + "--add-opens=java.base/java.nio=ALL-UNNAMED "
                                    + "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED");
            spark = builder.getOrCreate();
            logger.info("Spark session created successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Spark session: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Spark session", e);
        }
    }

    private void monitoringLoop() {
        try {
            while (running) {
                calculateAndSaveMetrics();
                Thread.sleep(60000); // Wait for 1 minute
            }
        } catch (InterruptedException e) {
            logger.warn("Monitoring thread interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Error in monitoring loop: {}", e.getMessage(), e);
        } finally {
            stopSparkSession();
            logger.info("Monitoring service shutdown completed");
        }
    }

    private void createRequiredTables() {
        System.out.println("Creating required tables if they don't exist");

        // Create users table
        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        national_id VARCHAR(255),
                        first_name VARCHAR(255),
                        last_name VARCHAR(255),
                        birth_date DATE,
                        address TEXT,
                        phone_number VARCHAR(20)
                    )
                """);

        // Create analysts table
        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS analysts (
                        id SERIAL PRIMARY KEY,
                        username VARCHAR(255),
                        api_key VARCHAR(255)
                    )
                """);

        System.out.println("Required tables created successfully");
    }

    @PreDestroy
    public void stopMonitoring() {
        running = false;
        if (monitoringThread != null && monitoringThread.isAlive()) {
            try {
                monitoringThread.join(5000); // Wait up to 5 seconds for the thread to finish
                logger.info("Monitoring thread stopped");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Interrupted while waiting for monitoring thread to stop");
            }
        }
        stopSparkSession();
    }

    private void stopSparkSession() {
        if (spark != null) {
            try {
                spark.stop();
                spark = null;
                logger.info("Spark session stopped successfully");
            } catch (Exception e) {
                logger.error("Error stopping Spark session: {}", e.getMessage(), e);
            }
        }
    }

    private void calculateAndSaveMetrics() {
        try {
            ensureSparkInitialized();
            // Read current data from the api_logs table in the database using Spark JDBC
            Dataset<Row> apiLogs = spark.read().format("jdbc")
                    .option("url", dbUrl)
                    .option("dbtable", "api_logs")
                    .option("user", dbUser)
                    .option("password", dbPassword)
                    .load();

            logger.info("Retrieved {} records from api_logs", apiLogs.count());

            // Calculate request rate with 5-minute increments
            Dataset<Row> requestRate = apiLogs
                    .withColumn("time_bucket", functions.window(functions.col("timestamp"), "5 minutes"))
                    .groupBy("time_bucket", "analyzer_id")
                    .count()
                    .withColumnRenamed("count", "request_count")
                    .select(
                            functions.col("time_bucket.start").as("calculation_time"),
                            functions.col("analyzer_id"),
                            functions.col("request_count")
                    );

            // Calculate average response time with 1-minute granularity
            Dataset<Row> avgResponseTime = apiLogs
                    .withColumn("time_bucket", functions.window(functions.col("timestamp"), "1 minute"))
                    .groupBy("time_bucket", "analyzer_id")
                    .agg(functions.avg("response_time_ms").alias("avg_response_time_ms"))
                    .select(
                            functions.col("time_bucket.start").as("calculation_time"),
                            functions.col("analyzer_id"),
                            functions.col("avg_response_time_ms")
                    );

            // Save metrics to OLAP system (using a simple table in the same database for demonstration)
            saveMetricsToOLAP(requestRate, "request_rate_metrics");
            saveMetricsToOLAP(avgResponseTime, "response_time_metrics");

            logger.info("Metrics calculated and saved successfully");
        } catch (Exception e) {
            logger.error("Error calculating and saving metrics: {}", e.getMessage(), e);
        }
    }

    private void saveMetricsToOLAP(Dataset<Row> metrics, String tableName) {
        metrics.write()
                .format("jdbc")
                .option("url", dbUrl)
                .option("dbtable", tableName)
                .option("user", dbUser)
                .option("password", dbPassword)
                .mode("append")
                .save();
    }

    public void logApiRequest(
            String analyzerId, String phoneNumber, long responseTime, String status, int requestSize
    ) {
        try {
            String sql = "INSERT INTO api_logs (timestamp, analyzer_id, phone_number, response_time_ms, status, "
                    + "request_size) " + "VALUES (NOW(), ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, analyzerId, phoneNumber, responseTime, status, requestSize);
            logger.info("Logged API " + "request " + "for " + "analyzer:" + " {}, " + "status: " + "{}", analyzerId,
                    status
            );
        } catch (Exception e) {
            logger.error("Error logging API request: {}", e.getMessage(), e);
        }
    }
}

