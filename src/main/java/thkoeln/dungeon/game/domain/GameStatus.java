package thkoeln.dungeon.game.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GameStatus {
    @JsonProperty("created")
    CREATED,
    @JsonProperty("started")
    RUNNING,
    @JsonProperty("ended")
    FINISHED;

    public boolean isActive() {
        return ( this.equals( CREATED ) || this.equals( RUNNING ) );
    }

    public boolean isOpenForJoining() {
        return ( this.equals( CREATED ) );
    }

}