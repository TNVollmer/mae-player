package thkoeln.dungeon.planet.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.domainprimitives.Coordinate;

import java.util.List;

/**
 * This service primarily aims at making sure that new planets our player learns about are properly connected to
 * each other, i.e. that a real interconnected map is created.
 * Please make sure not to use the PlanetRepository directly, but use the methods of this service instead.
 */
@Service
public class PlanetDomainService {
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



}
