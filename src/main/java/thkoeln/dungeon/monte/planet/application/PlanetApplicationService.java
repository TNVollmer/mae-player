package thkoeln.dungeon.monte.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.planet.domain.PlanetException;
import thkoeln.dungeon.monte.planet.domain.PlanetRepository;
import thkoeln.dungeon.monte.printer.finderservices.PlanetFinderService;

import javax.transaction.Transactional;
import java.util.*;

import static java.lang.Boolean.TRUE;

@Service
@Transactional
public class PlanetApplicationService implements PlanetFinderService {
    private Logger logger = LoggerFactory.getLogger( PlanetApplicationService.class );
    private PlanetRepository planetRepository;

    @Autowired
    public PlanetApplicationService( PlanetRepository planetRepository ) {
        this.planetRepository = planetRepository;
    }

    @Override
    public List<Planet> allPlanets() {
        List<Planet> allPlanets = planetRepository.findAll();
        /*
        for ( Planet planet : allPlanets ) {
            if ( !planet.checkBidirectionalRelationshipsWithNeighbours()) {
                throw new PlanetException( "allPlanets(): Unidirectional connection " + planet + " -> " + "neighbour");
            }
        }
        */
        return allPlanets;
    }

    @Override
    public List<Planet> allVisitedPlanets() {
        return planetRepository.findAllByVisitedIs( true );
    }


    public Optional<Planet> findById( UUID id ) {
        return planetRepository.findById( id );
    }


    public void save( Planet planet ) {
        if ( planet == null ) throw new PlanetException( "planet == null" );
        logger.debug( "Save " + planet );
        /*
        if ( !planet.checkBidirectionalRelationshipsWithNeighbours() ) {
            throw new PlanetException("save(...): Unidirectional connection " + planet + " -> " + "neighbour");
        }
        */
        planetRepository.save( planet );
    }


    /**
     * Save the "full circle" around this planet (this may be changed due propaging neighboring connections
     * and hard borders. Like this (P + all N are saved):
     *    N  --  N  --  N
     *    |      |      |
     *    N  --  P  --  N
     *    |      |      |
     *    N  --  N  --  N
     *
     * @param planet
     */
    public void savePlanetAndFullCircleAroundIt( Planet planet ) {
        if ( planet == null ) throw new PlanetException( "planet == null" );
        logger.info( "Save full circle around " + planet );
        save( planet );
        for ( CompassDirection direction : CompassDirection.values() ) {
            Planet neighbour = planet.getNeighbour( direction );
            if ( neighbour != null ) {
                save( neighbour );
                for ( CompassDirection diagonal : direction.ninetyDegrees() ) {
                    Planet diagonalNeighbour = neighbour.getNeighbour( diagonal );
                    if ( diagonalNeighbour != null ) {
                        save( diagonalNeighbour );
                    }
                }
            }
        }
    }


    /**
     * Add or update a planet we learn about from an external event.
     * @param planetId
     * @param movementDifficulty
     * @return
     */
    public Planet addOrUpdatePlanet( UUID planetId, Energy movementDifficulty ) {
        if ( planetId == null ) throw new PlanetException( "planetId == null" );
        logger.info("Add planet " + planetId + " with movement difficulty " + movementDifficulty );
        Optional<Planet> foundOptional = planetRepository.findByPlanetId( planetId );
        Planet newPlanet = foundOptional.isPresent() ? foundOptional.get() : new Planet(planetId);
        if ( movementDifficulty != null ) newPlanet.setMovementDifficulty( movementDifficulty );
        planetRepository.save( newPlanet );
        return newPlanet;
    }


    public void addPlanetNeighbours( PlanetDiscoveredEvent planetDiscoveredEvent ) {
        if ( planetDiscoveredEvent == null ) throw new PlanetException( "planetDiscoveredEvent == null" );
        UUID planetId = planetDiscoveredEvent.getPlanetId();
        logger.info( "Add neighbours for planet " + planetId );
        Optional<Planet> perhapsPlanet = planetRepository.findByPlanetId( planetId );
        if ( !perhapsPlanet.isPresent() ) {
            logger.error( "No planet found for ID " + planetId + "!" );
            return;
        }
        Planet planet = perhapsPlanet.get();
        Energy movementDifficulty = Energy.from( planetDiscoveredEvent.getMovementDifficulty() );
        planet.setMovementDifficulty( movementDifficulty );
        MineableResource mineableResource = planetDiscoveredEvent.getResource() == null ?
                null : MineableResource.fromTypeAndAmount(
                planetDiscoveredEvent.getResource().getResourceType(), planetDiscoveredEvent.getResource().getCurrentAmount() );
        planet.setMineableResource( mineableResource );
        Map<CompassDirection, Planet> directionPlanetMap = new HashMap<>();
        for ( PlanetNeighboursDto planetNeighboursDto : planetDiscoveredEvent.getNeighbours() ) {
            Planet neighbour = addOrUpdatePlanet( planetNeighboursDto.getId(), null );
            directionPlanetMap.put( planetNeighboursDto.getDirection(), neighbour );
        }
        planet.defineAllNeighbours( directionPlanetMap );
        savePlanetAndFullCircleAroundIt( planet );
    }


}