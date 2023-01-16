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

    @Setter ( AccessLevel.PROTECTED )
    private boolean spawnPoint = false;

    private boolean visited = false;

    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet northNeighbour = null;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet eastNeighbour = null;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet southNeighbour = null;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet westNeighbour = null;

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

    public static Planet spawnPoint( UUID planetId ) {
        Planet spawnPoint = new Planet( planetId );
        spawnPoint.setSpawnPoint( true );
        return spawnPoint;
    }



    public static Planet blackHole() {
        Planet blackHole = new Planet();
        blackHole.setPlanetId( null );
        blackHole.setSpawnPoint( false );
        blackHole.setVisited( false );
        return blackHole;
    }


    /**
     * A neighbour relationship is always set on BOTH sides.
     * @param otherPlanet
     * @param direction
     */
    public void defineNeighbour( Planet otherPlanet, CompassDirection direction ) {
        if ( otherPlanet == null ) throw new PlanetException( "Cannot establish neighbouring relationship with null planet!" ) ;
        try {
            Method otherGetter = neighbouringGetter( direction.getOppositeDirection() );
            Method setter = neighbouringSetter( direction );
            setter.invoke(this, otherPlanet );
            Planet remoteNeighbour = (Planet) otherGetter.invoke( otherPlanet );
            if ( !this.equals( remoteNeighbour ) ) {
                Method otherSetter = neighbouringSetter( direction.getOppositeDirection() );
                otherSetter.invoke( otherPlanet, this );
            }
        }
        catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new PlanetException( "Something went wrong that should not have happened ..." + e );
        }
        logger.info( "Established neighbouring relationship between planet '" + this + "' and '" + otherPlanet + "'." );
        closeNeighbouringCycleForAllDirectionsBut( direction );
    }


    public void closeNeighbouringCycleForAllDirectionsBut( CompassDirection notInThisDirection ) {
        for ( CompassDirection compassDirection: CompassDirection.values() ) {
            if ( compassDirection.equals( notInThisDirection ) ) continue;
            Planet neighbour = getNeighbour( compassDirection );
            if ( neighbour != null ) {
                for ( CompassDirection ninetyDegrees: compassDirection.ninetyDegrees() ) {
                    if( this.getNeighbour( ninetyDegrees ) != null &&
                            neighbour.getNeighbour( ninetyDegrees ) != null &&
                            this.getNeighbour( ninetyDegrees ).getNeighbour( compassDirection ) == null ) {
                        this.getNeighbour( ninetyDegrees ).defineNeighbour(
                                neighbour.getNeighbour( ninetyDegrees ), compassDirection );
                        logger.info( "Closed cycle ..." );
                    }
                }
            }
        }
    }


    public void resetAllNeighbours() {
        setNorthNeighbour( null );
        setWestNeighbour( null );
        setEastNeighbour( null );
        setSouthNeighbour( null );
    }


    public Planet getNeighbour( CompassDirection compassDirection ) {
        try {
            Method getter = neighbouringGetter( compassDirection );
            return (Planet) getter.invoke( this );
        }
        catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new PlanetException( "Something went wrong that should not have happened ..." + e.getStackTrace() );
        }
    }

    protected Method neighbouringGetter(CompassDirection direction ) throws NoSuchMethodException {
        String name = "get" + WordUtils.capitalize( WordUtils.swapCase( String.valueOf( direction ) ) ) + "Neighbour";
        return this.getClass().getDeclaredMethod( name );
    }


    protected Method neighbouringSetter( CompassDirection direction ) throws NoSuchMethodException {
        String name = "set" + WordUtils.capitalize( WordUtils.swapCase( String.valueOf( direction ) ) ) + "Neighbour";
        return this.getClass().getDeclaredMethod( name, new Class[]{ this.getClass() } );
    }


    public Map<CompassDirection, Planet> allNeighbours() {
        Map<CompassDirection, Planet> allNeighboursMap = new HashMap<>();
        if ( getNorthNeighbour() != null ) allNeighboursMap.put( NORTH, getNorthNeighbour() );
        if ( getWestNeighbour() != null ) allNeighboursMap.put( WEST, getWestNeighbour() );
        if ( getEastNeighbour() != null ) allNeighboursMap.put( EAST, getEastNeighbour() );
        if ( getSouthNeighbour() != null ) allNeighboursMap.put( SOUTH, getSouthNeighbour() );
        return allNeighboursMap;
    }



    public void fillEmptyNeighbourSlotsWithBlackHoles() {
        if ( getNorthNeighbour() == null ) setNorthNeighbour( Planet.blackHole() );
        if ( getWestNeighbour() == null ) setWestNeighbour( Planet.blackHole() );
        if ( getEastNeighbour() == null ) setEastNeighbour( Planet.blackHole() );
        if ( getSouthNeighbour() == null ) setSouthNeighbour( Planet.blackHole() );
    }



    /**
     * @return A map with all neighbouring PlanetPrintables, in each direction.
     */
    @Override
    public Map<MapDirection, PlanetPrintable> neighbourMap() {
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

    public Boolean isSpaceStation() { return spawnPoint; }

    public void setSpawnPoint( Boolean isSpaceStation ) {
        if ( isSpaceStation != null && isSpaceStation ) {
            spawnPoint = true;
            visited = true;
        }
    }


    @Override
    public boolean hasBeenVisited() { return visited; }


    @Override
    public boolean isBlackHole() {
        return planetId == null;
    }


    /**
     * @return a random neighbour that hasn't been visited yet. If all neighbours have been visited,
     * return a random visited neighbour. If there is no neighbour, return an arbitrary neighbour.
     * If there is no neighbour, return null.
     */
    public Planet findUnvisitedNeighbourOrAnyIfAllVisited() {
        Collection<Planet> neighbours = allNeighbours().values();
        Planet lastCheckedNeighbour = null;
        for ( Planet neighbour : neighbours ) {
            if ( !neighbour.isBlackHole() ) lastCheckedNeighbour = neighbour;
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
        String whoAmI = isSpaceStation() ? "#" : "_";
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
}
