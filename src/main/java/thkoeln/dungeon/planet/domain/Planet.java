package thkoeln.dungeon.planet.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.domainprimitives.CompassDirection;
import thkoeln.dungeon.domainprimitives.Coordinate;
import thkoeln.dungeon.domainprimitives.MineableResource;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Planet {
    @Id
    private final UUID id = UUID.randomUUID();

    // this is the EXTERNAL id that we receive from MapService. We could use this also as our own id, but then
    // we'll run into problems in case MapService messes up their ids. So, better we better keep these two apart.
    private UUID planetId;

    @Setter
    @Getter ( AccessLevel.NONE ) // just because Lombok generates the ugly getSpacestation()
    private Boolean spacestation = Boolean.FALSE;
    public Boolean isSpaceStation() { return spacestation; }

    @Getter ( AccessLevel.NONE ) // just because Lombok generates the ugly getVisited()
    private Boolean visited = Boolean.FALSE;
    public Boolean hasBeenVisited() { return visited; }

    @Setter // really?
    @Embedded
    private Coordinate coordinate = null;

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
    @Setter
    private MineableResource mineableResource;

    @Transient
    private Logger logger = LoggerFactory.getLogger( Planet.class );

    public Planet( UUID planetId ) {
        this.planetId = planetId;
    }

    public static Planet createFirstSpacestation( UUID planetId ) {
        return null;
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
            throw new PlanetException( "Something went wrong that should not have happened ..." + e.getStackTrace() );
        }
        logger.info( "Established neighbouring relationship between planet '" + this + "' and '" + otherPlanet + "'." );
    }


    public void resetAllNeighbours() {
        setNorthNeighbour( null );
        setWestNeighbour( null );
        setEastNeighbour( null );
        setSouthNeighbour( null );
    }


    protected Method neighbouringGetter( CompassDirection direction ) throws NoSuchMethodException {
        String name = "get" + WordUtils.capitalize( WordUtils.swapCase( String.valueOf( direction ) ) ) + "Neighbour";
        return this.getClass().getDeclaredMethod( name );
    }


    protected Method neighbouringSetter( CompassDirection direction ) throws NoSuchMethodException {
        String name = "set" + WordUtils.capitalize( WordUtils.swapCase( String.valueOf( direction ) ) ) + "Neighbour";
        return this.getClass().getDeclaredMethod( name, new Class[]{ this.getClass() } );
    }


    public List<Planet> allNeighbours() {
        List<Planet> allNeighbours = new ArrayList<>();
        if ( getNorthNeighbour() != null ) allNeighbours.add( getNorthNeighbour() );
        if ( getWestNeighbour() != null ) allNeighbours.add( getWestNeighbour() );
        if ( getEastNeighbour() != null ) allNeighbours.add( getEastNeighbour() );
        if ( getSouthNeighbour() != null ) allNeighbours.add( getSouthNeighbour() );
        return allNeighbours;
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

    @Override
    public String toString() {
        if ( coordinate != null ) return coordinate.toString();
        return "";
    }
}
