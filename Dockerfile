FROM maven:3-eclipse-temurin-22 as build
ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn clean package

FROM openjdk:23-jdk
COPY --from=build app/target/*.jar app/player-skeleton-java-springboot.jar
ENTRYPOINT java -jar /app/player-skeleton-java-springboot.jar