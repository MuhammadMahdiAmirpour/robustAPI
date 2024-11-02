# Use a base image with Java installed
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the jar file into the container
COPY robustAPI/target/robustAPI-0.0.1-SNAPSHOT.jar robustAPI-0.0.1-SNAPSHOT.jar

# Set environment variables for database connection if needed
ENV SPRING_DATASOURCE_URL=jdbc:mysql://your-db-url:3306/robustapidb
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=RoottooR123$

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "robustAPI-0.0.1-SNAPSHOT.jar"]
