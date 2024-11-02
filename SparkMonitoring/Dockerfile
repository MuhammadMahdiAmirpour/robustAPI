FROM docker.arvancloud.ir/bitnami/spark:3.5.1
WORKDIR /app

COPY SparkMonitoring/target/SparkMonitoring-1.0-SNAPSHOT.jar /app/spark-monitoring-tool.jar
COPY SparkMonitoring/target/dependency/mysql-connector-java-8.0.27.jar /app/mysql-connector-java.jar

# Submit the Spark job with the MySQL connector in the classpath
CMD ["spark-submit", \
     "--driver-class-path", "/app/mysql-connector-java.jar", \
     "--jars", "/app/mysql-connector-java.jar", \
     "--class", "com.github.monitoring.SparkMonitoringTool", \
     "/app/spark-monitoring-tool.jar"]
