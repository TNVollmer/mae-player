package thkoeln.dungeon.monte.planet.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.printer.printables.MineableResourcePrintable;
import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.printer.util.MapDirection;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.Boolean.*;
import static thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection.*;

@Entity
@Getter
@Setter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Planet implements PlanetPrintable {
    @Id
    private final UUID id = UUID.randomUUID();

    // this is the EXTERNAL id that we receive from MapService. We could use this also as our own id, but then
    // we'll run into problems in case MapService messes up their ids. So, better we better keep these two apart.
    private UUID planetId;

    private boolean visited = false;

    @OneToOne ( cascade = CascadeType.MERGE)
    //@Setter ( AccessLevel.PROTECTED )
    private Planet northNeighbour = null;
    @OneToOne ( cascade = CascadeType.MERGE)
    //@Setter ( AccessLevel.PROTECTED )
    private Planet eastNeighbour = null;
    @OneToOne ( cascade = CascadeType.MERGE)
    //@Setter ( AccessLevel.PROTECTED )
    private Planet southNeighbour = null;
    @OneToOne ( cascade = CascadeType.MERGE)
    //@Setter ( AccessLevel.PROTECTED )
    private Planet westNeighbour = null;

    private Boolean northHardBorder = null;
    private Boolean eastHardBorder = null;
    private Boolean southHardBorder = null;
    private Boolean westHardBorder = null;

    @Embedded
    private MineableResource mineableResource;

    @Embedded
    private Energy movementDifficulty;

    @Transient
    private Logger logger = LoggerFactory.getLogger( Planet.class );


    public Planet( UUID planetId ) {
        this.planetId = planetId;
        this.movementDifficulty = Energy.zero();
    }


    /**
     * This is to be called when an event is received that clearly and completely lists all the
     * neighbours. Then it can be concluded that the missing neighbours must now be hard borders.
     * @param directionPlanetMap
     */
    public void defineAllNeighbours( Map<CompassDirection, Planet> directionPlanetMap ) {
        if ( directionPlanetMap == null ) throw new PlanetException( "directionPlanetMap == null" ) ;
        for ( CompassDirection direction : CompassDirection.values() ) {
            Planet potentialNeighbour = directionPlanetMap.get( direction );
            if ( potentialNeighbour != null ) {
                defineNeighbour( potentialNeighbour, direction );
            }
        }
        defineEmptyNeighbourSlotsAsHardBorders();
    }


    /**
     * A neighbour relationship is always set on BOTH sides.
     * @param otherPlanet
     * @param direction
     */
    public void defineNeighbour( Planet otherPlanet, CompassDirection direction ) {
        logger.info( "Analyse connection in " + direction + " for " + this + " <-> " + otherPlanet + "..." );
        if ( otherPlanet == null ) throw new PlanetException( "otherPlanet == null" ) ;
        Planet currentNeighbour = getNeighbour( direction );
        if ( currentNeighbour != null && !currentNeighbour.equals( otherPlanet ) ) {
            logger.warn(this + " has already a " + direction +
                    " connection to " + currentNeighbour + ", now is reset to " + otherPlanet);
        }
        Planet thisShouldBeMe = otherPlanet.getNeighbour( direction.getOppositeDirection() );
        if ( thisShouldBeMe != null && !this.equals( thisShouldBeMe ) ) {
            logger.warn( otherPlanet + " should have a " + direction + " connection to " + this
                    + ", but actually it was to " + thisShouldBeMe );
        }
        setBidirectionalNeighbourConnectionAt( direction, otherPlanet );
        propagateNeighbouringConnectionAlong( direction );
        setHardBorder( direction, FALSE );
        propagateHardBordersAlong( direction );
    }


    /**
     * This is to be called when an event is received that clearly and completely lists all the
     * neighbours. Then it can be concluded that the missing neighbours must now be hard borders.
     */
    protected void defineEmptyNeighbourSlotsAsHardBorders() {
        for ( CompassDirection direction : CompassDirection.values() ) {
            if ( getNeighbour( direction ) == null ) {
                setHardBorder( direction, TRUE );
            }
        }
    }


    /**
     * Close cycle connections in patterns like these:
     *  diagNeigh  ???   P
     *     |             |
     *    this --dir-->  neighbour
     */
    private void propagateNeighbouringConnectionAlong( CompassDirection direction ) {
        Planet neighbour = getNeighbour( direction );
        if ( neighbour == null ) return;
        for ( CompassDirection diagonalDirection: direction.ninetyDegrees() ) {
            Planet diagonalNeighbour = getNeighbour( diagonalDirection );
            if( diagonalNeighbour != null && neighbour.getNeighbour( diagonalDirection ) != null ) {
                diagonalNeighbour.setBidirectionalNeighbourConnectionAt(
                        direction, neighbour.getNeighbour( diagonalDirection ) );
                logger.info( "Closed cycle " + direction + " for " +
                        diagonalNeighbour + " <-> " + neighbour.getNeighbour( diagonalDirection ) );
            }
        }
    }


    /**
     *  Propagate hard borders in patterns like these:
     *  diagNeigh --  rectNeighbour
     *     |                ???
     *    this --dir-->  Black Hole
     */
    private void propagateHardBordersAlong( CompassDirection direction ) {
        if ( getHardBorder( direction ) != TRUE ) return;
        for ( CompassDirection diagonalDirection: direction.ninetyDegrees() ) {
            Planet diagonalNeighbour = getNeighbour( diagonalDirection );
            Planet rectNeighbour = diagonalNeighbour != null ? diagonalNeighbour.getNeighbour( direction ) : null;
            if( rectNeighbour != null &&
                    rectNeighbour.getHardBorder( diagonalDirection.getOppositeDirection() ) == FALSE ) {
                throw new PlanetException( "SNAFU in the hard border config!" );
            }
            if( rectNeighbour != null &&
                    rectNeighbour.getHardBorder( diagonalDirection.getOppositeDirection() ) == null ) {
                rectNeighbour.setHardBorder( diagonalDirection.getOppositeDirection(), TRUE );
                logger.info( "Marked hard border on " + rectNeighbour + " in "
                        + diagonalDirection.getOppositeDirection() );
            }
        }
    }


    /**
     * Intended as a security method, to run regularly over all planets
     */
    public boolean checkBidirectionalRelationshipsWithNeighbours() {
        for ( CompassDirection direction : CompassDirection.values() ) {
            Planet neighbour = getNeighbour( direction );
            if ( neighbour != null ) {
                Planet thisShouldBeMe = neighbour.getNeighbour( direction.getOppositeDirection() );
                if ( !this.equals( thisShouldBeMe ) ) {
                    logger.debug( "Planet check: " + this + " -> " + neighbour + " is unidirectional!" );
                    return false;
                }
            }
        }
        return true;
    }


    public Planet getNeighbour( CompassDirection direction ) {
        if ( direction == null ) throw new PlanetException( "direction == null" );
        try {
            Method getter = directionalGetter( direction, "Neighbour" );
            return (Planet) getter.invoke( this );
        }
        catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new PlanetException( "Something went wrong that should not have happened ..." + e.getStackTrace() );
        }
    }


    public void setNeighbour( CompassDirection direction, Planet planet ) {
        if ( direction == null || planet == null ) throw new PlanetException( "direction == null || planet == null" );
        try {
            Method setter = directionalSetter( direction, "Neighbour", this.getClass() );
            setter.invoke( this, planet );
        }
        catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new PlanetException( "Something went wrong that should not have happened ..." + e.getStackTrace() );
        }
    }


    public void setBidirectionalNeighbourConnectionAt( CompassDirection direction, Planet planet ) {
        if ( direction == null || planet == null ) throw new PlanetException( "direction == null || planet == null" );
        setNeighbour( direction, planet );
        planet.setNeighbour( direction.getOppositeDirection(), this );
        logger.info( "Set connection in " + direction + " for " + this + " <-> " + planet );
    }


    public Boolean getHardBorder( CompassDirection direction ) {
        if ( direction == null ) throw new PlanetException( "direction == null" );
        try {
            Method getter = directionalGetter( direction, "HardBorder" );
            return (Boolean) getter.invoke( this );
        }
        catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new PlanetException( "Something went wrong that should not have happened ..." + e.getStackTrace() );
        }
    }


    public void setHardBorder( CompassDirection direction, Boolean hardBorderFlag ) {
        if ( direction == null ) throw new PlanetException( "direction == null" );
        try {
            Method setter = directionalSetter( direction, "HardBorder", Boolean.class );
            setter.invoke( this, hardBorderFlag );
        }
        catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new PlanetException( "Something went wrong that should not have happened ..." + e.getStackTrace() );
        }
    }

    protected Method directionalGetter( CompassDirection direction, String property )
            throws NoSuchMethodException {
        String name = "get" + WordUtils.capitalize( WordUtils.swapCase( String.valueOf( direction ) ) )
                + property;
        return this.getClass().getDeclaredMethod( name );
    }


    protected Method directionalSetter( CompassDirection direction, String property, Class parameterClass )
            throws NoSuchMethodException {
        String name = "set" + WordUtils.capitalize( WordUtils.swapCase( String.valueOf( direction ) ) )
                + property;
        return this.getClass().getDeclaredMethod( name, parameterClass );
    }


    public Map<CompassDirection, Planet> allNeighbours() {
        Map<CompassDirection, Planet> allNeighboursMap = new HashMap<>();
        if ( getNorthNeighbour() != null ) allNeighboursMap.put( NORTH, getNorthNeighbour() );
        if ( getWestNeighbour() != null ) allNeighboursMap.put( WEST, getWestNeighbour() );
        if ( getEastNeighbour() != null ) allNeighboursMap.put( EAST, getEastNeighbour() );
        if ( getSouthNeighbour() != null ) allNeighboursMap.put( SOUTH, getSouthNeighbour() );
        return allNeighboursMap;
    }



    /**
     * @return neighbouring PlanetPrintables in each direction. Only real planets count, no black holes.
     * Null means "no known neighbour in this direction".
     *
     * NOTE: Must be consistent with hardBorders(). If neighbours() has a planet in direction d,
     * then hardBorders() must have the value FALSE in this direction.
     */
    @Override
    public Map<MapDirection, PlanetPrintable> neighbours() {
        Map<MapDirection, PlanetPrintable> neighbourMap = new HashMap<>();
        if ( getNorthNeighbour() != null ) neighbourMap.put( MapDirection.no, getNorthNeighbour() );
        if ( getWestNeighbour() != null ) neighbourMap.put( MapDirection.we, getWestNeighbour() );
        if ( getEastNeighbour() != null ) neighbourMap.put( MapDirection.ea, getEastNeighbour() );
        if ( getSouthNeighbour() != null ) neighbourMap.put( MapDirection.so, getSouthNeighbour() );
        return neighbourMap;
    }

    public boolean hasNeighbours() {
        return allNeighbours().size() > 0;
    }


    /**
     * @return Information for each direction if there is a hard border (the edges of the map,
     * or a black hole).
     * TRUE = there is a hard border
     * FALSE = there is no hard border, and we are sure of it => there is a planet there.
     * Null = we don't know yet, maybe hard border, maybe not.
     *
     * NOTE: Must be consistent with neighbours(). If neighbours() has a planet in direction d,
     * then hardBorders() must have the value FALSE in this direction.
     */
    public Map<MapDirection, Boolean> hardBorders() {
        Map<MapDirection, Boolean> hardBorderMap = new HashMap<>();
        hardBorderMap.put( MapDirection.no, getNorthHardBorder() );
        hardBorderMap.put( MapDirection.we, getWestHardBorder() );
        hardBorderMap.put( MapDirection.ea, getEastHardBorder() );
        hardBorderMap.put( MapDirection.so, getSouthHardBorder() );
        return hardBorderMap;
    }


    @Override
    public boolean hasBeenVisited() { return visited; }


    /**
     * @return a random neighbour that hasn't been visited yet. If all neighbours have been visited,
     * return a random visited neighbour. If there is no neighbour, return an arbitrary neighbour.
     * If there is no neighbour, return null.
     */
    public Planet findUnvisitedNeighbourOrAnyIfAllVisited() {
        Collection<Planet> neighbours = allNeighbours().values();
        Planet lastCheckedNeighbour = null;
        for ( Planet neighbour : neighbours ) {
            lastCheckedNeighbour = neighbour;
            if ( lastCheckedNeighbour != null && !lastCheckedNeighbour.hasBeenVisited() ) break;
        }
        return lastCheckedNeighbour;
    }





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Planet)) return false;
        Planet planet = (Planet) o;
        return Objects.equals(id, planet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    /**
     * @return The short name of a planet when printed on a map.
     * IMPORTANT: Name must be <= 4 chars, otherwise the layout breaks.
     */
    @Override
    public String mapName() {
        Character whoAmI = (mineableResource == null || mineableResource.isEmpty()) ? '_' : mineableResource.key();
        return whoAmI + String.valueOf( planetId ).substring( 0, 3 );
    }


    @Override
    public String detailedDescription() {
        String printString =  toString() + " (";
        List<String> attributeStrings = new ArrayList<>();
        if ( !hasBeenVisited() ) attributeStrings.add( "??" );
        if ( mineableResource != null ) attributeStrings.add( mineableResource.toString() );

        Map<CompassDirection, Planet> allNeighbours = allNeighbours();
        for ( CompassDirection direction : CompassDirection.values() ) {
            if ( allNeighbours.containsKey( direction) ) {
                attributeStrings.add( direction.toStringShort() + ": " + allNeighbours.get( direction ) );
            }
        }
        printString += String.join( ", ", attributeStrings.toArray( new String[attributeStrings.size()] ) ) + ")";
        return printString;
    }


    /**
     * @return The mineable resource printable, if this planet _has_ a resource. Otherwise, just return null.
     */
    @Override
    public MineableResourcePrintable mineableResourcePrintable() {
        return mineableResource;
    }


    @Override
    public String toString() {
        return mapName();
    }


    public void setNorthNeighbour( Planet northNeighbour ) {
        logger.debug( "XXX " + this + " setNorthNeighbour: " + northNeighbour);
        this.northNeighbour = northNeighbour;
    }

    public void setEastNeighbour( Planet eastNeighbour ) {
        logger.debug( "XXX " + this + " setEastNeighbour: " + eastNeighbour);
        this.eastNeighbour = eastNeighbour;
    }

    public void setSouthNeighbour( Planet southNeighbour ) {
        logger.debug( "XXX " + this + " setSouthNeighbour: " + southNeighbour);
        this.southNeighbour = southNeighbour;
    }

    public void setWestNeighbour( Planet westNeighbour ) {
        logger.debug( "XXX " + this + " setWestNeighbour: " + westNeighbour);
        this.westNeighbour = westNeighbour;
    }
}
