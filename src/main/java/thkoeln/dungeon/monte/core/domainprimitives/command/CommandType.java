package thkoeln.dungeon.monte.core.domainprimitives.command;


/**
 * Domain Primitive to represent a command type
 */
public enum CommandType {
    MOVEMENT ( "movement" ),
    BATTLE ( "battle" ),
    MINING ( "mining" ),
    REGENERATE ( "regenerate" ),
    BUYING ( "buying" ),
    SELLING ( "selling" );

    private final String stringValue;

    private CommandType( String s ) {
        stringValue = s;
    }

    public String getStringValue() {
        return stringValue;
    }
}
