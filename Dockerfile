FROM maven:latest

# Set the working directory
WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/player.jar

ENTRYPOINT ["java", "-jar", "/app/player.jar"]
