package com.github.robustapi.util;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final Faker faker = new Faker();

    @Autowired
    public DataInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        createRequiredTables(); // Create tables at startup
        createDummyData(); // Create dummy data when application starts
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

    private void createDummyData() {
        System.out.println("Creating dummy data...");

        // Insert dummy users
        List<Object[]> userBatchArgs = new ArrayList<>();

        for (int i = 0; i < 10_000_000; i++) { // Adjust as necessary for your needs
            String nationalId = String.valueOf(faker.number().randomNumber(9, true));
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String birthDate = faker.date().birthday().toString();
            String address = faker.address().fullAddress();
            String phoneNumber = faker.phoneNumber().cellPhone();

            // Add to batch arguments
            userBatchArgs.add(new Object[]{nationalId, firstName, lastName, birthDate, address, phoneNumber});

            if (i % 100000 == 0) { // Log progress every 100,000 records
                System.out.printf("Generated %d users.%n", i);
            }
        }

        // Perform batch insert for users
        String userSql = "INSERT INTO users (national_id, first_name, last_name, birth_date, address, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(userSql, userBatchArgs);

        // Insert dummy analysts
        List<Object[]> analystBatchArgs = new ArrayList<>();

        for (int i = 0; i < 2000; i++) { // Adjust as necessary for your needs
            String username = faker.name().username();
            String apiKey = faker.internet().uuid(); // Generate a random UUID as an API key

            // Add to batch arguments
            analystBatchArgs.add(new Object[]{username, apiKey});

            if (i % 100 == 0) { // Log progress every 100 records
                System.out.printf("Generated %d analysts.%n", i);
            }
        }

        // Perform batch insert for analysts
        String analystSql = "INSERT INTO analysts (username, api_key) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(analystSql, analystBatchArgs);

        System.out.println("Dummy data created successfully.");
    }
}
