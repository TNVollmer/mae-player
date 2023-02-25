FROM maven:latest

# Set the working directory
WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/PlayerMONTE-0.0.1-SNAPSHOT.jar

# Expose port 8999 for the WebSocket connection
EXPOSE 8999
ENTRYPOINT ["java", "-jar", "-Dserver.port=8999", "/app/PlayerMONTE-0.0.1-SNAPSHOT.jar"]