package thkoeln.dungeon.monte.core.domainprimitives.purchasing;


public enum CapabilityType {
    DAMAGE( "DA" ),
    ENERGY_REGEN( "ER" ),
    HEALTH( "H" ),
    MAX_ENERGY( "ME" ),
    MINING( "MI" ),
    MINING_SPEED( "MS" ),
    STORAGE( "S" );

    private final String stringValue;

    private CapabilityType( String s ) {
        stringValue = s;
    }

    public String toString() {
        return stringValue;
    }

}
