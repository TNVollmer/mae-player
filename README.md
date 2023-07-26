# Player Skeleton with Java and Spring Boot

This is a player skeleton for the microservice dungeon, which is written in Java using Spring Boot.
You can use this player as a basis for your own player.

Requirements:
- Java 11

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

Authors: Stefan Bente, Philipp Schmeier
