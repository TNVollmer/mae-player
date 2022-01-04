package thkoeln.dungeon.planet.domain;

public enum CompassDirection {
    north, east, south, west;

    public CompassDirection getOppositeDirection() {
        switch( this ) {
            case north: return south;
            case east: return west;
            case south: return north;
            case west: return east;
        }
        return null;
    }
}
