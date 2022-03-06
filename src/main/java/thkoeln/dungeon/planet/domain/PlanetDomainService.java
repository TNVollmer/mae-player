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

    public void addPlanet( Planet newPlanet ) {
        List<Planet> foundPlanets = planetRepository.findAll();
        if( foundPlanets.isEmpty() ) {
            newPlanet.setCoordinate( Coordinate.initialCoordinate() );
            newPlanet.resetAllNeighbours();
        }
        else {
            // this is not yet properly thought through ... need to clarify first in which order you learn about
            // space stations. Do you need to listen to spacestation events by Map?
        }
        planetRepository.save( newPlanet );
    }



}
