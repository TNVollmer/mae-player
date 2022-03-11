package thkoeln.dungeon.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.domainprimitives.TwoDimDynamicArray;
import thkoeln.dungeon.domainprimitives.Coordinate;
import thkoeln.dungeon.planet.domain.Planet;
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
            TwoDimDynamicArray<Planet> island = new TwoDimDynamicArray<>( 1, 1 );
            island.put( Coordinate.initialCoordinate(), spacestation );
            planetMap.put( spacestation, island );
        }
        for ( Planet spacestation: spacestations ) {
            if ( !spacestation.getTemporaryProcessingFlag() ) {
                // not already visited, i.e. this is really an island (= partial graph)
                TwoDimDynamicArray<Planet> island = new TwoDimDynamicArray<>(1, 1);
                island.put( Coordinate.initialCoordinate(), spacestation );
                spacestation.constructLocalIsland( island, Coordinate.initialCoordinate() );
                planetMap.put( spacestation, island );
            }
        }
        return planetMap;
    }
}