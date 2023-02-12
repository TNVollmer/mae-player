package thkoeln.dungeon.monte.printer.printers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.printer.devices.OutputDevice;
import thkoeln.dungeon.monte.printer.finderservices.PlanetFinderService;
import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.printer.util.TwoDimDynamicArray;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * OutputDevice class to output the current player status to console.
 */
@Service
public class PlanetPrinter {
    private Logger logger = LoggerFactory.getLogger( PlanetPrinter.class );
    private PlanetFinderService planetFinderService;
    private List<OutputDevice> outputDevices;


    @Autowired
    public PlanetPrinter( PlanetFinderService planetFinderService,
                          List<OutputDevice> outputDevices ) {
        this.planetFinderService = planetFinderService;
        this.outputDevices = outputDevices;
    }



    /**
     * @return Print all currently alive robots, in a compact format suitable for the console.
     */
    public void printPlanetList() {
        outputDevices.forEach(p -> p.header( "Known planets" ) );
        List<? extends PlanetPrintable> planetPrintables = planetFinderService.allPlanets();
        outputDevices.forEach(p -> p.startBulletList() );
        for ( PlanetPrintable planetPrintable : planetPrintables) {
            outputDevices.forEach(p -> p.writeBulletItem( planetPrintable.detailedDescription() ) );
        }
        outputDevices.forEach(p -> p.endBulletList() );
    }


    /**
     * Create a list of "local maps" around the space stations, as long as there are several partial maps
     * (i.e. the whole universe has not yet been explored).
     */
    public List<TwoDimDynamicArray<PlanetPrintable>> allPlanetClusters() {
        List<TwoDimDynamicArray<PlanetPrintable>> allPlanetClusters = new ArrayList<>();

        List<? extends PlanetPrintable> allPlanets = planetFinderService.allPlanets();
        for ( PlanetPrintable planet: allPlanets ) {
            Predicate<TwoDimDynamicArray<PlanetPrintable>> containsPlanet =
                    planetCluster -> planetCluster.contains( planet );
            boolean alreadyThere = allPlanetClusters.stream().anyMatch( containsPlanet );
            if ( ! alreadyThere ) {
                // this spawnPoint is not yet part of any previous cluster. Therefore, we start a new one.
                PlanetMapConstructor planetMapConstructor = new PlanetMapConstructor( planet );
                TwoDimDynamicArray<PlanetPrintable> newPlanetCluster = planetMapConstructor.constructLocalClusterMap();
                allPlanetClusters.add( newPlanetCluster );
            }
        }
        return allPlanetClusters;
    }



}
