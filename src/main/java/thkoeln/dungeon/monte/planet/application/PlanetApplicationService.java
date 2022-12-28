package thkoeln.dungeon.monte.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.domainprimitives.CompassDirection;
import thkoeln.dungeon.monte.domainprimitives.MovementDifficulty;
import thkoeln.dungeon.monte.domainprimitives.TwoDimDynamicArray;
import thkoeln.dungeon.monte.domainprimitives.Coordinate;
import thkoeln.dungeon.monte.eventlistener.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.monte.eventlistener.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.monte.eventlistener.concreteevents.robot.RobotPlanetDto;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.planet.domain.PlanetException;
import thkoeln.dungeon.monte.planet.domain.PlanetRepository;

import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

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
     * Add or update a planet (may be space station) we learn about from an external event,
     * without having any information about its neighbours. That could be e.g. when
     * new space stations are declared.
     * @param robotPlanetDto - the RobotSpawnedEvent
     * @param isSpaceStation (null if unknown)
     * @return the found planet
     */
    public Planet addOrUpdatePlanetFromEvent( RobotPlanetDto robotPlanetDto, Boolean isSpaceStation ) {
        if ( robotPlanetDto == null ) throw new PlanetException( "robotPlanetDto == null" );
        UUID planetId = robotPlanetDto.getPlanetId();
        MovementDifficulty movementDifficulty = MovementDifficulty.fromInteger( robotPlanetDto.getMovementDifficulty() );
        return addOrUpdatePlanet( planetId, movementDifficulty, isSpaceStation );
    }


    /**
     * Add or update a planet (may be space station) we learn about from an external event.
     * @param planetId
     * @param movementDifficulty
     * @param isSpaceStation
     * @return the found planet
     */
    public Planet addOrUpdatePlanet( UUID planetId, MovementDifficulty movementDifficulty, Boolean isSpaceStation ) {
        if ( planetId == null ) throw new PlanetException( "planetId == null" );
        logger.info("Add planet " + planetId + " with movement difficulty " + movementDifficulty  +
                " (space station: " + isSpaceStation + ")");
        Optional<Planet> foundOptional = planetRepository.findByPlanetId(planetId);
        Planet newPlanet = foundOptional.isPresent() ? foundOptional.get() : new Planet(planetId);
        if ( isSpaceStation != null ) newPlanet.setSpacestation( isSpaceStation );
        if ( movementDifficulty != null ) newPlanet.setMovementDifficulty( movementDifficulty );
        planetRepository.save(newPlanet);
        return newPlanet;
    }


    public void addPlanetNeighbours( PlanetDiscoveredEvent planetDiscoveredEvent ) {
        if ( planetDiscoveredEvent == null || !planetDiscoveredEvent.isValid() )
            throw new PlanetException( "planetDiscoveredEvent == null || !planetDiscoveredEvent.isValid()" );
        UUID planetId = planetDiscoveredEvent.getPlanetId();
        logger.info( "Add neighbour for planet " + planetId );
        Optional<Planet> perhapsPlanet = planetRepository.findByPlanetId( planetId );
        if ( !perhapsPlanet.isPresent() ) {
            logger.error( "No planet found for ID " + planetId + "!" );
            return;
        }
        Planet planet = perhapsPlanet.get();
        if ( planet.hasNeighbours() ) {
            logger.error( "Neighbours for planet " + planet + " have already been set - not safe to do it twice." );
            return;
        }
        MovementDifficulty movementDifficulty =
                MovementDifficulty.fromInteger( planetDiscoveredEvent.getMovementDifficulty() );
        planet.setMovementDifficulty( movementDifficulty );
        for ( PlanetNeighboursDto planetNeighboursDto : planetDiscoveredEvent.getNeighbours() ) {
            Planet neighbour = addOrUpdatePlanet( planetNeighboursDto.getId(), null, null );
            planet.defineNeighbour( neighbour, planetNeighboursDto.getDirection() );
            planetRepository.save( neighbour );
        }
        planetRepository.save( planet );
    }


    /**
     * Add neighbour as this comes in from an event
     * @param planetId
     * @param neighbourId
     * @param direction
     */
    public void ____addNeighbourToPlanet_OLD( UUID planetId, UUID neighbourId, CompassDirection direction ) {
        logger.info( "Add neighbour " + neighbourId + " in " + direction + " to planet " + planetId );
        Planet planet = planetRepository.findByPlanetId( planetId )
                .orElseThrow( () -> new PlanetException( "Planet with UUID " + planetId + " not found!" ) );
        Optional<Planet> neighbourOpt = planetRepository.findByPlanetId( neighbourId );
        Planet neighbour = neighbourOpt.isPresent() ? neighbourOpt.get() : new Planet( neighbourId );
        planet.defineNeighbour( neighbour, direction );
        planetRepository.save( planet );
        planetRepository.save( neighbour );
    }



    // todo this doesn't belong here
    public void visitPlanet( UUID planetId, Integer movementDifficulty ) {
        logger.info( "Visit planet " + planetId + " with movement difficulty " + movementDifficulty );
        Planet planet = planetRepository.findByPlanetId( planetId )
                .orElseThrow( () -> new PlanetException( "Planet with UUID " + planetId + " not found!" ) );
        planet.setVisited( true );
        planet.setMovementDifficulty( MovementDifficulty.fromInteger( movementDifficulty ) );
        planetRepository.save( planet );
    }


    /**
     * Method to create arrays for display of the planet map
     */
    public Map<Planet, TwoDimDynamicArray<Planet>> allPlanetsAsClusterMap() {
        Map<Planet, TwoDimDynamicArray<Planet>> planetMap = new HashMap<>();
        List<Planet> allPlanets = planetRepository.findAll();
        for ( Planet planet: allPlanets ) {
            planet.setTemporaryProcessingFlag( FALSE );
            planetRepository.save( planet );
        }
        // create this as a Map of space stations (which are the first planets known to the player) pointing
        // to a local 2d array containing all planets connected to that space station.
        // When two such "planet clusters" are discovered to be connected, one of it is taken out of the map
        // (to avoid printing planets twice)
        List<Planet> spacestations = planetRepository.findBySpacestationEquals( TRUE );
        for ( Planet spacestation: spacestations ) {
            if ( !spacestation.getTemporaryProcessingFlag() ) {
                TwoDimDynamicArray<Planet> planetCluster = new TwoDimDynamicArray<>( spacestation );
                // not already visited, i.e. this is really a planet cluster (= partial graph)
                spacestation.constructLocalCluster( planetCluster, Coordinate.initialCoordinate() );
                planetMap.put( spacestation, planetCluster );
            }
        }
        for ( Planet planet : allPlanets ) planetRepository.save( planet );
        return planetMap;
    }
}