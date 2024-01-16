package thkoeln.dungeon.player.core.domainprimitives.location;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum to represent different types of mineable resources
 */
public enum MineableResourceType {
    COAL( "coal" ),
    IRON( "iron" ),
    GEM( "gem" ),
    GOLD( "gold" ),
    PLATIN( "platin" );

    private final String stringValue;

    MineableResourceType( String stringValue ) {
        this.stringValue = stringValue;
    }

    @JsonValue
    public String getStringValue() {
        return stringValue;
    }
}
