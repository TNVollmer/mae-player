FROM maven:latest
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} PlayerMONTE-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/PlayerMONTE-0.0.1-SNAPSHOT.jar"]