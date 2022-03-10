package thkoeln.dungeon.domainprimitives;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public int xOffset() {
        switch( this ) {
            case NORTH: return 0;
            case EAST: return 1;
            case SOUTH: return 0;
            case WEST: return -1;
        }
        return 0;
    }

    public int yOffset() {
        switch( this ) {
            case NORTH: return 1;
            case EAST: return 0;
            case SOUTH: return -1;
            case WEST: return 0;
        }
        return 0;
    }

    public List<CompassDirection> ninetyDegrees() {
        List<CompassDirection> retVals = new ArrayList<>();
        switch( this ) {
            case NORTH:
                retVals.add( WEST );
                retVals.add( EAST );
                break;
            case EAST:
                retVals.add( NORTH );
                retVals.add( SOUTH );
                break;
            case SOUTH:
                retVals.add( WEST );
                retVals.add( EAST );
                break;
            case WEST:
                retVals.add( NORTH );
                retVals.add( SOUTH );
        }
        return retVals;
    }


}
