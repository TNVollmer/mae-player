# Player Skeleton with Java and Spring Boot

This is a player skeleton for the microservice dungeon, which is written in Java using Spring Boot.
You can use this player as a basis for your own player.

Requirements:
- Java 17

## Preparation

To use this skeleton as the base for your player development, you need to accomplish the following steps.

First, fork this repository and create a new repository under the [Player Teams subgroup](https://gitlab.com/the-microservice-dungeon/player-teams) which is named after your desired player name, for example `player-constantine`.
Now you need to add your player-name to a few files. The required places are marked using TODO comments.
Update the files in `helm-chart/Chart.yaml`, `pom.xml`, `src/resources/application.properties` and `.gitlab-ci.yml`.

## Configuration

The player can be configured using environment variables

| Environment Variable | Default                                       |
|----------------------| --------------------------------------------- |
| RABBITMQ_HOST        | localhost                                     |
| RABBITMQ_PORT        | 5672                                          |
| RABBITMQ_USER        | admin                                         |
| RABBITMQ_PASSWORD    | admin                                         |
| GAME_HOST            | http://localhost:8080                         |
| PLAYER_NAME          | layer-skeleton-typescript-nodejs              |
| PLAYER_EMAIL         | player-skeleton-typescript-nodejs@example.com |
| LOGGING_LEVEL        | debug                                         |

## Usage

The player can be started in starts the player in a dev mode. 
This mode creates and starts a player automatically for you when you start the player. 
No manual requests are required.
This feature is **ONLY FOR LOCAL DEVELOPMENT**.
To enable it, activate the `dev` Spring Boot profile. 
You can achieve it over the command line by setting the `spring.profiles.active` property or 
instruct your IDE (e.g [IntelliJ](https://www.jetbrains.com/help/idea/run-debug-configuration-spring-boot.html#modify-options)) 
to activate it. 

Authors: <Add your authors name here>
