package thkoeln.dungeon.game.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GameStatus {
    @JsonProperty("created")
    CREATED,
    @JsonProperty("started")
    RUNNING,
    @JsonProperty("ended")
    FINISHED,
    ORPHANED // this is the state a game takes when the GameService doesn't list it anymore

}