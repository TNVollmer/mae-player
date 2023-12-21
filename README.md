# Player Skeleton with Java and Spring Boot

This is a player skeleton for the microservice dungeon, which is written in Java using Spring Boot.
You can use this player as a basis for your own player. 

Authors: Tobias Hund, Stefan Bente

Requirements:
- Java 17

## Preparation

To use this skeleton as the base for your player development, you need to accomplish the following steps.

First, fork this repository and create a new repository under 
the [Player Teams subgroup](https://gitlab.com/the-microservice-dungeon/player-teams) (or in any other Git location).
The fork should named after your desired player name, for example `player-constantine`.

Now you need to add your player-name to a few files. The required places are marked using TODO comments.
Update the files in `helm-chart/Chart.yaml`, `pom.xml`, `src/resources/application.properties` and `.gitlab-ci.yml`.


## Configuration

The player can be configured using environment variables:

| Environment Variable | Default                                      |
|----------------------|----------------------------------------------|
| RABBITMQ_HOST        | localhost                                    |
| RABBITMQ_PORT        | 5672                                         |
| RABBITMQ_USER        | admin                                        |
| RABBITMQ_PASSWORD    | admin                                        |
| GAME_HOST            | http://localhost:8080                        |
| PLAYER_NAME          | player-skeleton-java-springboot              |
| PLAYER_EMAIL         | player-skeleton-java-springboot@example.com  |
| LOGGING_LEVEL        | debug                                        |


## Event Listening

The skeleton player makes use of a single messaging queue for all events. It automatically listens
on the player-owned queue for all events and deserializes them. Afterwards the events are dispatched
using the [in-memory eventing system provided by the Spring framework](https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html).
See the [RabbitMQ Listener implementation](src/main/java/thkoeln/dungeon/player/player/application/PlayerEventListener.java)
and exemplary [in-memory event listeners](src/main/java/thkoeln/dungeon/player/game/application/GameEventListener.java).

## Dev Mode

The player can be started **dev mode**. This mode creates and starts a game automatically for you, when you 
start the player. No manual requests are required. This is _REALLY_ helpful for local development, as 
you can start the player and the game with a single command, from within your IDE.

This feature is **ONLY FOR LOCAL DEVELOPMENT**. To enable it, activate the `dev` Spring Boot profile. 
You can achieve it over the command line by setting the `spring.profiles.active` property or 
instruct your IDE (e.g [IntelliJ](https://www.jetbrains.com/help/idea/run-debug-configuration-spring-boot.html#modify-options)) to activate it. 

### Enable Dev Mode in IntelliJ Ultimate

#### 1. Create a Spring Boot run configuration for your main class (if not already done)

Easiest way is to right click on the main class `thkoeln.dungeon.player.DungeonPlayerMainApplication` 
and select "Run DungeonPlayerMainApplication ..." in the context menu.
 
![Create Spring Boot run configuration](/images/activate-dev-mode0.png)

#### 2. Open the run configuration editor ...

![Create Spring Boot run configuration](/images/activate-dev-mode1.png)

#### 3. ... and add the `dev` profile to the active profiles

![Create Spring Boot run configuration](/images/activate-dev-mode2.jpg)


## Tests

The player skeleton contains a number of tests, which can be enhanced to test your own player implementation.
