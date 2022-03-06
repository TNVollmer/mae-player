package thkoeln.dungeon.domainprimitives;

public enum CompassDirection {
    NORTH, EAST, SOUTH, WEST;

    public CompassDirection getOppositeDirection() {
        switch( this ) {
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case SOUTH: return NORTH;
            case WEST: return EAST;
        }
        return null;
    }
}
