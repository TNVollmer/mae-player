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
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.RobotPlanetDto;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.planet.domain.PlanetException;
import thkoeln.dungeon.monte.planet.domain.PlanetRepository;
import thkoeln.dungeon.monte.printer.finderservices.PlanetFinderService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Boolean.TRUE;

@Service
public class PlanetApplicationService implements PlanetFinderService {
    private Logger logger = LoggerFactory.getLogger( PlanetApplicationService.class );
    private PlanetRepository planetRepository;

    @Autowired
    public PlanetApplicationService( PlanetRepository planetRepository ) {
        this.planetRepository = planetRepository;
    }

    @Override
    public List<Planet> allPlanets() {
        return planetRepository.findAll();
    }

    @Override
    public List<Planet> allSpawnPoints() {
        return planetRepository.findBySpawnPointEquals( true );
    }

    public Optional<Planet> findById( UUID id ) {
        return planetRepository.findById( id );
    }


    public void save( Planet planet ) {
        planetRepository.save( planet );
    }


    public void savePlanetAndNeighbours( Planet planet ) {
        planetRepository.save( planet );
        for ( Planet neighbour : planet.allNeighbours().values() ) {
            planetRepository.save( neighbour );
        }
    }



    /**
     * Add or update a planet (may be space station) we learn about from an external event,
     * without having any information about its neighbours. That could be e.g. when
     * new space stations are declared.
     * @param robotPlanetDto - the RobotSpawnedEvent
     * @param isSpaceStation (null if unknown)
     * @return the found planet
     */
    public Planet addOrUpdatePlanet( RobotPlanetDto robotPlanetDto, Boolean isSpaceStation ) {
        if ( robotPlanetDto == null ) throw new PlanetException( "robotPlanetDto == null" );
        UUID planetId = robotPlanetDto.getPlanetId();
        Energy movementDifficulty = Energy.from( robotPlanetDto.getMovementDifficulty() );
        return addOrUpdatePlanet( planetId, movementDifficulty, isSpaceStation );
    }


    /**
     * Add or update a planet (may be space station) we learn about from an external event.
     * @param planetId
     * @param movementDifficulty
     * @param isSpaceStation
     * @return the found planet
     */
    public Planet addOrUpdatePlanet( UUID planetId, Energy movementDifficulty, Boolean isSpaceStation ) {
        if ( planetId == null ) throw new PlanetException( "planetId == null" );
        logger.info("Add planet " + planetId + " with movement difficulty " + movementDifficulty  +
                " (space station: " + isSpaceStation + ")");
        Optional<Planet> foundOptional = planetRepository.findByPlanetId(planetId);
        Planet newPlanet = foundOptional.isPresent() ? foundOptional.get() : new Planet(planetId);
        if ( isSpaceStation != null ) newPlanet.setSpawnPoint( isSpaceStation );
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
        Energy movementDifficulty = Energy.from( planetDiscoveredEvent.getMovementDifficulty() );
        planet.setMovementDifficulty( movementDifficulty );
        MineableResource mineableResource = MineableResource.fromTypeAndAmount(
                planetDiscoveredEvent.getResource().getResourceType(), planetDiscoveredEvent.getResource().getCurrentAmount() );
        planet.setMineableResource( mineableResource );
        for ( PlanetNeighboursDto planetNeighboursDto : planetDiscoveredEvent.getNeighbours() ) {
            Planet neighbour = addOrUpdatePlanet( planetNeighboursDto.getId(), null, null );
            planet.defineNeighbour( neighbour, planetNeighboursDto.getDirection() );
        }
        planet.fillEmptyNeighbourSlotsWithBlackHoles();
        savePlanetAndNeighbours( planet );
    }



}