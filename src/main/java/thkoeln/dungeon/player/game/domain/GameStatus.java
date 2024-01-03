package thkoeln.dungeon.player.game.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GameStatus {
    @JsonProperty("created")
    CREATED,
    @JsonProperty("started")
    STARTED,
    @JsonProperty("ended")
    ENDED;

    public boolean isActive() {
        return (this.equals(CREATED) || this.equals(STARTED));
    }

    public boolean isOpenForJoining() {
        return (this.equals(CREATED));
    }

    public boolean isRunning() {
        return (this.equals(STARTED));
    }
}
