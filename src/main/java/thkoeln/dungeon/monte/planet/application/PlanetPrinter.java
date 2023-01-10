package thkoeln.dungeon.monte.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.printer.OutputDevice;
import thkoeln.dungeon.monte.core.util.TwoDimDynamicArray;
import thkoeln.dungeon.monte.planet.domain.Planet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * OutputDevice class to output the current player status to console.
 */
@Service
public class PlanetPrinter {
    private Logger logger = LoggerFactory.getLogger( PlanetPrinter.class );
    private PlanetApplicationService planetApplicationService;
    private List<OutputDevice> outputDevices;


    @Autowired
    public PlanetPrinter( PlanetApplicationService planetApplicationService,
                          List<OutputDevice> outputDevices) {
        this.planetApplicationService = planetApplicationService;
        this.outputDevices = outputDevices;
    }



    /**
     * @return Print all currently alive robots, in a compact format suitable for the console.
     */
    public void printPlanetList() {
        outputDevices.forEach(p -> p.header( "Known planets" ) );
        List<Planet> planets = planetApplicationService.allPlanets();
        outputDevices.forEach(p -> p.startBulletList() );
        for ( Planet planet : planets) {
            outputDevices.forEach(p -> p.writeBulletItem( planet.toStringDetailed() ) );
        }
        outputDevices.forEach(p -> p.endBulletList() );
    }


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
