FROM docker.arvancloud.ir/openjdk:17-jdk-slim

RUN mkdir /app && chmod +rwx /app && \
    apt-get update && apt-get install -y wget && \
    wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.27/mysql-connector-java-8.0.27.jar -O /app/mysql-connector-java.jar && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY robustAPI/target/robustAPI-0.0.1-SNAPSHOT.jar robustAPI-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="--add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
    --add-opens=java.base/java.lang=ALL-UNNAMED \
    --add-opens=java.base/java.nio=ALL-UNNAMED \
    --add-exports=java.base/sun.nio.ch=ALL-UNNAMED"

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "robustAPI-0.0.1-SNAPSHOT.jar"]