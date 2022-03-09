package thkoeln.dungeon.planet.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.domainprimitives.CompassDirection;
import thkoeln.dungeon.domainprimitives.Coordinate;
import thkoeln.dungeon.domainprimitives.MovementDifficulty;
import thkoeln.dungeon.eventconsumer.game.GameEventConsumerService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This service primarily aims at making sure that new planets our player learns about are properly connected to
 * each other, i.e. that a real interconnected map is created.
 * Please make sure not to use the PlanetRepository directly, but use the methods of this service instead.
 */
@Service
public class PlanetDomainService {
    private Logger logger = LoggerFactory.getLogger( PlanetDomainService.class );
    private PlanetRepository planetRepository;

    @Autowired
    public PlanetDomainService( PlanetRepository planetRepository ) {
        this.planetRepository = planetRepository;
    }

    /**
     * Add a new space station we learn about from an external event
     * @param newSpaceStation
     */
    public void addSpaceStation( Planet newSpaceStation ) {
        logger.info( "Add space station " + newSpaceStation );
        List<Planet> foundPlanets = planetRepository.findAll();
        if( foundPlanets.isEmpty() ) {
            newSpaceStation.setCoordinate( Coordinate.initialCoordinate() );
            newSpaceStation.resetAllNeighbours();
        }
        else {
            // this is not yet properly thought through ... need to clarify first in which order you learn about
            // space stations. Do you need to listen to spacestation events by Map?
        }
        planetRepository.save( newSpaceStation );
    }

    public void visitPlanet( UUID planetId, Integer movementDifficulty ) {
        logger.info( "Visit planet " + planetId + " with movement difficulty " + movementDifficulty );
        Planet planet = planetRepository.findByPlanetId( planetId )
                .orElseThrow( () -> new PlanetException( "Planet with UUID " + planetId + " not found!" ) );
        planet.setVisited( true );
        planet.setMovementDifficulty( MovementDifficulty.fromInteger( movementDifficulty ) );
        planetRepository.save( planet );
    }


    public void addNeighbourToPlanet( UUID planetId, UUID neighbourId, CompassDirection direction, Integer movementDifficulty ) {
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

}
