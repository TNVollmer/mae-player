# Generic Player for _The Microservice Dungeon_

This repo contains a generic, Java-based Dungeon player template. It can be used to build an own player based on it. 

### What it has:

- Some basic domain model (see below)
- Some REST call implementations from player to Game service
- Some basic event listeners
- Player registration, and some logic to listen to a game creation and start
- An improvised logfile read endpoint: call `GET <server>:<yourport>/actuator/logs` and get a printout from the
logfile (in local dev env, you of course see this on your console, but in a deployed environment, this comes
handy)

### What it doesn't have:

- Basically anything else. 
- (to be described in more detail)

### Will it be developed any further?

For the time being, yes. 



## How to use it

Fork it into your own repo.


### How can I merge later changes to the generic player into my own repo?

This is what you need to do, step by step, using git bash and Intellij for merging. 

#### 1. Set up a branch to get the changes (just safer)

* `git checkout -b new_version`
* `git push --set-upstream origin new_version`

#### 2. Get the remote changes

* `git remote add generic_player https://gitlab.com/the-microservice-dungeon/generic-player.git` 
* `git fetch generic_player`

#### 3. Merge the changes

* `git merge generic_player/main --allow-unrelated-histories`

This will (probably) tell you that there are merge conflicts. Open Intellij, go in main menu to 
`Git >> Resolve Conflicts ...` and click "Merge" for each file listed there. You get a 3-panel window: 
- left are your additions to a common baseline
- middle is the result
- right is the generic player source

Resolve it step by step and then test.



