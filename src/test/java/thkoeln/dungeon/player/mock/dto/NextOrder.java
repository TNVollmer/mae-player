package thkoeln.dungeon.player.mock.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NextOrder {
    MOVE_NORTH,
    MOVE_EAST,
    MOVE_WEST,
    MOVE_SOUTH,
    ATTACK,
    REGENERATE,
    BUY_HEALTH,
    BUY_ENERGY;

    @JsonValue
    public String mapToJacksonValue() {
        return name().toLowerCase();
    }

}
