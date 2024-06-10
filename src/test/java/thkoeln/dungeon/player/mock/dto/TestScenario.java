package thkoeln.dungeon.player.mock.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TestScenario {
    FIGHT, MAP_EXPLORATION;

    @JsonValue
    public String mapToJacksonValue() {
        return name().toLowerCase();
    }
}
