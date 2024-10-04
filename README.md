# Java Player for the microservice dungeon

This player is based of the [java player skeleton](https://gitlab.com/the-microservice-dungeon/player-teams/skeletons/player-java-springboot) from Tobias Hund and Stefan Bente

Requirements:
- Java 17

[Gitlab Version (with pipeline)](https://gitlab.com/TNVollmer/mea-java-player)

## Concept

This is a player which has specialized robots that can focus on specific task prioritise upgrades needed for those tasks.
Furthermore, was the goal to keep the Spring Boot database and not replace it with an inmemory solution to not exceed the command sending time during round start.

## Money Management

The money on income is separate in three budgets:
- New Robots
- Upgrades
- Misc (mostly for healing)

The budget for new robots fills with 20% of the incoming money if the budget is less than 1000.
60% of the remaining money goes in the budget for upgrading and 40% in the budget for new robots

## Robots

### Robot Types

Robots can be one of three Types:
- Scout
- Miner
- Warrior

The player always wants to have one scout and is usually the first robot the player buys. 
If the scout count is satisfied the next robot will be a miner if they make up less than 60% of all robots else the next robot is a warrior.

#### Scouts
Scouts exist to explore all planets. If all planets are explored all scouts will turn into warriors.

#### Miners
Miners prioritise mining the best resource they are able to mine. After a mining upgrade the robot will sell his entire inventory before moving to the next optimal planet for mining.
If the robot can not reach a planet with the best resource it can mine it will try to find one through exploring unexplored planets.

#### Warriors

Warriors randomly move from planet to planet and attack enemy robots if they are on the same planet.

### Upgrading
Each robot type has their own priority for upgrading capabilities. Those Priorities will all be raised by one level, so that no capability has a level difference greater than one.
When the capabilities are maxed out the robot will upgrade the remaining capabilities in the same way as prioritised ones.

Priorities are settable, see [Customization](#customization)

Upgrades like commands are queued in order to reduce time needed on round start

### Customization

This player offers some variety for customisation in `application.properties`

````properties
# Choose between MINER, SCOUT and WARRIOR
robot.defaultType=MINER

# The maximum count of robots for the type
robot.minerMaxCount=0
robot.scoutMaxCount=4
robot.warriorMaxCount=10

# The percentage of the robot type until the maximum count is reached
robot.minerPercentage=70
robot.scoutPercentage=30
robot.warriorPercentage=20

# After how many total robots the type will be created
robot.createMinersAfter=0
robot.createScoutsAfter=0
robot.createWarriorsAfter=10

# Prioritized upgrades, will be upgraded in the given order
robot.scoutUpgradeOrder=ENERGY_REGEN,MAX_ENERGY,HEALTH
robot.minerUpgradeOrder=MINING_SPEED,MINING,STORAGE,ENERGY_REGEN,MAX_ENERGY,HEALTH
robot.warriorUpgradeOrder=DAMAGE,ENERGY_REGEN,MAX_ENERGY,HEALTH
````

### Commands

To reduce the time needed at round start, has every robot its own command queue. This queue is filled, if the queue is empty, after the robot completed its previous command (at the end of events like `RobotMovedEvent`)

## Map

Each planet knows its neighboring planets. To find a path to a planet, a breadth-first search is used.

## On Game Start

Instead of the usual money management the player uses all money to create robots.
In case of 500 the player will buy 5 robots. Those robots consist of at least one scout and miners and warriors, depending on what percentages are set.

## Known Issue

Due to how this player is designed to keep the database, it is possible to experience some issues in the late game (about 50+ robots), in which the player can't keep up with the time needed to send commands and the available time to do so.
This may also cause issues with the money management or the robot behaviour itself.