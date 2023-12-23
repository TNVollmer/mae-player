package thkoeln.dungeon.player.core.events.concreteevents.game;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RoundStatusType {
    @JsonProperty("started")
    STARTED("started"),

    @JsonProperty("command input ended")
    COMMAND_INPUT_ENDED("command input ended"),

    @JsonProperty("ended")
    ENDED("ended");

    private final String stringValue;

    RoundStatusType(String s) {
        stringValue = s;
    }
}
