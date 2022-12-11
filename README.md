(this is outdated and needs to be revised!)

# Generic Player template for _The Microservice Dungeon_

This repo contains a generic, Java-based Dungeon player template. It can be used to build an own player based on it. 

**What it has:**

- Some basic domain model (see below)
- Some REST call implementations from player to Game service (caveat: unit-tested, but not yet integration-tested)
- Some basic event listeners
- Player registration, and some logic to listen to a game creation and start
- An improvised logfile read endpoint: call `GET <server>:<yourport>/actuator/logs` and get a printout from the
logfile (in local dev env, you of course see this on your console, but in a deployed environment, this comes
handy)

**What it doesn't have:**

- Basically anything else. The robots don't have attributes, they don't do anything. There is no money yet. Planets
  are just UUIDs. 
- This means: All the rest is yours to implement. 

**Will it be developed any further?**

No. We will "iron out the kinks", meaning: We will make sure that the REST calls implemented here, and the listeners, 
work properly with the rest of all services. On top of that, no further features will be added.


## How to use it


### In `https://github.com/The-Microservice-Dungeon/generic-player`, click "Use this template"

![howto-1](images/howto1.png)

### Enter your repo's name. Three things are important:

1. The repo name must start with "player-"
2. The repo must be created in the organization, **NOT in your own Github account**.
3. Make the repo private.

![howto-2](images/howto2.png)

### Edit the README ...

![howto-3](images/howto3.png)

### ... and replace it with your own documentation. Important: Add your team names as authors!

![howto-4](images/howto4.png)



## How can I merge later changes to the template into my own repo?

The way to do this is described [here](https://stackoverflow.com/questions/56577184/github-pull-changes-from-a-template-repository). In short:
* Follow the steps above to create your own repo based on the template
* Go to the repo
* `git remote add template https://github.com/The-Microservice-Dungeon/generic-player.git`
* `git fetch --all`
* `git merge template/main --allow-unrelated-histories`



## Architecture 

The domain model is as such:

![Domain Model](model/Player-Domain-Model.png)

The packages are designed around the aggregates, according the basic DDD approach to software architecture. 
This leads to the following internal package dependency structure. 
* Connection to the Apache Kafka is abstracted through the `eventconsumer` package
* Connection to Game service (via REST) is handled in the `restadapter` package


![Package Dependencies](model/Package-Dependencies.png)



## What do I need to do to create my own player based on this?

The Game team has created a great "cheat sheet" which I take the liberty to reproduce here. It gives a good impression
of the necessary steps. 

![Cheat Sheet](images/Game_Bedienungsanleitung.jpg)
