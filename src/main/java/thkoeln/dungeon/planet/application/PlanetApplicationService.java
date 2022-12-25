package thkoeln.dungeon.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.domainprimitives.CompassDirection;
import thkoeln.dungeon.domainprimitives.MovementDifficulty;
import thkoeln.dungeon.domainprimitives.TwoDimDynamicArray;
import thkoeln.dungeon.domainprimitives.Coordinate;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetDomainService;
import thkoeln.dungeon.planet.domain.PlanetException;
import thkoeln.dungeon.planet.domain.PlanetRepository;

import java.util.*;

@Service
public class PlanetApplicationService {
    private Logger logger = LoggerFactory.getLogger( PlanetApplicationService.class );
    private PlanetRepository planetRepository;


    @Autowired
    public PlanetApplicationService( PlanetRepository planetRepository ) {
        this.planetRepository = planetRepository;
    }


    public List<Planet> findAll() {
        return planetRepository.findAll();
    }

    public Optional<Planet> findById( UUID id ) {
        return planetRepository.findById( id );
    }

    public void save( Planet planet ) {
        planetRepository.save( planet );
    }


    /**
     * Add a new planet (may be space station) we learn about from an external event,
     * without having any information about its neighbours. That could be e.g. when
     * new space stations are declared.
     * @param newPlanetId
     */
    public Planet addPlanetWithoutNeighbours( UUID newPlanetId, boolean isSpaceStation ) {
        Planet newPlanet = null;
        List<Planet> foundPlanets = planetRepository.findAll();
        if( foundPlanets.isEmpty() ) {
            // no planets yet. Assign (0,0) to this first one.
            newPlanet = new Planet( newPlanetId );
        }
        else {
            Optional<Planet> foundOptional = planetRepository.findByPlanetId( newPlanetId );
            if( foundOptional.isPresent() ) {
                // not sure if this can happen ... but just to make sure, all the same.
                newPlanet = foundOptional.get();
            }
            else {
                newPlanet = new Planet( newPlanetId );
            }
        }
        newPlanet.setSpacestation( isSpaceStation );
        planetRepository.save( newPlanet );
        return newPlanet;
    }


    public void visitPlanet( UUID planetId, Integer movementDifficulty ) {
        logger.info( "Visit planet " + planetId + " with movement difficulty " + movementDifficulty );
        Planet planet = planetRepository.findByPlanetId( planetId )
                .orElseThrow( () -> new PlanetException( "Planet with UUID " + planetId + " not found!" ) );
        planet.setVisited( true );
        planet.setMovementDifficulty( MovementDifficulty.fromInteger( movementDifficulty ) );
        planetRepository.save( planet );
    }


    public void addNeighbourToPlanet(UUID planetId, UUID neighbourId, CompassDirection direction, Integer movementDifficulty ) {
        logger.info( "Add neighbour " + neighbourId + " with movement difficulty " + movementDifficulty
                + " in " + direction + " to planet " + planetId );
        Planet planet = planetRepository.findByPlanetId( planetId )
                .orElseThrow( () -> new PlanetException( "Planet with UUID " + planetId + " not found!" ) );
        Optional<Planet> neighbourOpt = planetRepository.findByPlanetId( neighbourId );
        Planet neighbour = neighbourOpt.isPresent() ? neighbourOpt.get() : new Planet( neighbourId );
        neighbour.setMovementDifficulty( MovementDifficulty.fromInteger( movementDifficulty ) );
        planet.defineNeighbour( neighbour, direction );
        planetRepository.save( planet );
        planetRepository.save( neighbour );
    }


    /**
     * Method to create arrays for display of the planet map
     */
    public Map<Planet, TwoDimDynamicArray<Planet>> allPlanetsAs2DArrays() {
        Map<Planet, TwoDimDynamicArray<Planet>> planetMap = new HashMap<>();
        List<Planet> allPlanets = planetRepository.findAll();
        for ( Planet planet: allPlanets ) {
            planet.setTemporaryProcessingFlag( Boolean.FALSE );
            planetRepository.save( planet );
        }
        // create this as a Map of space stations (which are the first planets known to the player) pointing
        // to a local 2d array containing all planets connected to that space station. When two such "islands" are
        // discovered to be connected, one of it is taken out of the map (to avoid printing planets twice)
        List<Planet> spacestations = planetRepository.findBySpacestationEquals( Boolean.TRUE );
        for ( Planet spacestation: spacestations ) {
            if ( !spacestation.getTemporaryProcessingFlag() ) {
                TwoDimDynamicArray<Planet> island = new TwoDimDynamicArray<>( spacestation );
                // not already visited, i.e. this is really an island (= partial graph)
                spacestation.constructLocalIsland( island, Coordinate.initialCoordinate() );
                planetMap.put( spacestation, island );
            }
        }
        for ( Planet planet : allPlanets ) planetRepository.save( planet );
        return planetMap;
    }
}