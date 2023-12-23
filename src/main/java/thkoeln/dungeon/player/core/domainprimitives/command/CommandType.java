package thkoeln.dungeon.player.core.domainprimitives.command;


import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Domain Primitive to represent a command type
 */
public enum CommandType {
    MOVEMENT("movement"),
    BATTLE("battle"),
    MINING("mining"),
    REGENERATE("regenerate"),
    BUYING("buying"),
    SELLING("selling");

    private final String stringValue;

    CommandType(String s) {
        stringValue = s;
    }

    @JsonValue
    public String getStringValue() {
        return stringValue;
    }
}
