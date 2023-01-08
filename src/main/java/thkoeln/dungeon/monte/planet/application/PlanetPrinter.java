package thkoeln.dungeon.monte.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.util.TwoDimDynamicArray;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.core.util.ConsolePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Printer class to output the current player status to console.
 */
@Service
public class PlanetPrinter {
    private Logger logger = LoggerFactory.getLogger( PlanetPrinter.class );
    private PlanetApplicationService planetApplicationService;

    @Autowired
    public PlanetPrinter( PlanetApplicationService planetApplicationService ) {
        this.planetApplicationService = planetApplicationService;
    }



    /**
     * @return Print all currently alive robots, in a compact format suitable for the console.
     */

/*
    public void printPlanetList() {
        writeLine( "Known planets:" );
        List<Planet> planets = planetApplicationService.allPlanets();
        for ( Planet planet : planets) {
            writeLineIndent( planet.toStringDetailed() );
        }
    }
*/

    /**
     * Create a list of "local maps" around the space stations, as long as there are several partial maps
     * (i.e. the whole universe has not yet been explored).
     */


    public List<TwoDimDynamicArray<Planet>> allPlanetClusters() {
        List<TwoDimDynamicArray<Planet>> allPlanetClusters = new ArrayList<>();

        List<Planet> spacestations = planetApplicationService.allSpaceStations();
        for ( Planet spacestation: spacestations ) {
            Predicate<TwoDimDynamicArray<Planet>> containsSpacestation =
                    planetCluster -> planetCluster.contains( spacestation );
            boolean alreadyThere = allPlanetClusters.stream().anyMatch( containsSpacestation );
            if ( ! alreadyThere ) {
                // this spacestation is not yet part of any previous cluster. Therefore, we start a new one.
                TwoDimDynamicArray<Planet> newPlanetCluster = spacestation.constructLocalClusterMap();
                allPlanetClusters.add( newPlanetCluster );
            }
        }
        return allPlanetClusters;
    }



}
