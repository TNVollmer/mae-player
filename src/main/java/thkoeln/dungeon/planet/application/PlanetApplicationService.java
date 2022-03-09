package thkoeln.dungeon.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.domainprimitives.CompassDirection;
import thkoeln.dungeon.domainprimitives.Coordinate;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetDomainService;
import thkoeln.dungeon.planet.domain.PlanetException;
import thkoeln.dungeon.planet.domain.PlanetRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanetApplicationService {
    private Logger logger = LoggerFactory.getLogger( PlanetApplicationService.class );
    private PlanetRepository planetRepository;

    @Autowired
    public PlanetApplicationService( PlanetRepository planetRepository ) {
        this.planetRepository = planetRepository;
    }

    /**
     * Add a new planet (may be space station) we learn about from an external event,
     * without having any information about its neighbours. That could be e.g. when
     * new space stations are declared.
     * @param newPlanetId
     */
    public void addPlanetWithoutNeighbours( UUID newPlanetId, boolean isSpaceStation ) {
        Planet newPlanet = null;
        List<Planet> foundPlanets = planetRepository.findAll();
        if( foundPlanets.isEmpty() ) {
            // no planets yet. Assign (0,0) to this first one.
            newPlanet = new Planet( newPlanetId );
            newPlanet.setCoordinate( Coordinate.initialCoordinate() );
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
    }


    public void planetHasBeenVisited( UUID planetId, Integer movementDifficultyAsInt, String planetTypeAsString ) {
        // set movement difficulty
    }


    public void learnAboutNeighboursOfVisitedPlanet() {

    }


    /**
     * Add a new space station we learn about from an external event
     * TODO JUST A FRAGMENT
     * @param newSpaceStationUUID
     */
    public void addPlanetWithNeighbour( UUID newSpaceStationUUID ) {
        List<Planet> foundPlanets = planetRepository.findAll();
        if( foundPlanets.isEmpty() ) {
            // no planets yet. Assign (0,0) to this first one.
            Planet newSpaceStation = new Planet( newSpaceStationUUID );
            newSpaceStation.setCoordinate( Coordinate.initialCoordinate() );
            newSpaceStation.setSpacestation( true );
            planetRepository.save( newSpaceStation );
        }
        else {
            Optional<Planet> foundOptional = planetRepository.findById( newSpaceStationUUID );
            if( foundOptional.isPresent() ) {
                // not sure if this can happen ... but just to make sure, all the same.
                Planet spacestation = foundOptional.get();
                spacestation.setSpacestation( true );
                planetRepository.save( spacestation );
            }
            else {
                //planetDomainService.addNewPlanetToMap
            }

        }
    }
}
