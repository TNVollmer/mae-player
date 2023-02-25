FROM maven:latest

# Set the working directory
WORKDIR /app

# Copy the HTML and JavaScript files into the container
COPY ./src/main/webapp/index.html /app/
COPY ./src/main/webapp/sockjs-0.3.4.js /app/
COPY ./src/main/webapp/stomp.js /app/
COPY ./src/main/webapp/style.css /app/

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/PlayerMONTE-0.0.1-SNAPSHOT.jar

# Expose port 8999 for the WebSocket connection
EXPOSE 8999
ENTRYPOINT ["java", "-jar", "-Dserver.port=8999", "/app/PlayerMONTE-0.0.1-SNAPSHOT.jar"]