package thkoeln.dungeon.planet.domain;

import lombok.AccessLevel;
import lombok.Getter;
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
public class Planet {
    @Id
    private final UUID id = UUID.randomUUID();

    @Setter
    private String name;

    @Setter
    @Getter ( AccessLevel.NONE )
    private Boolean spacestation = Boolean.FALSE;

    @Setter
    @Embedded
    private Coordinate coordinate;

    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet northNeighbour;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet eastNeighbour;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet southNeighbour;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet westNeighbour;

    @Embedded
    @Setter
    private MineableResource mineableResource;

    @Transient
    private Logger logger = LoggerFactory.getLogger( Planet.class );

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

    public Boolean isSpaceStation() { return spacestation; }


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
        return getName() + " (" + getId() + ")";
    }
}
