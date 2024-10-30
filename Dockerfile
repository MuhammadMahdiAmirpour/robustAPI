# Use the official OpenJDK image as a base
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven build output (JAR file) into the container
COPY target/robustAPI-0.0.1-SNAPSHOT.jar robustAPI-0.0.1-SNAPSHOT.jar

# Expose the port on which the app will run
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "robustAPI-0.0.1-SNAPSHOT.jar"]
