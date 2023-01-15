package thkoeln.dungeon.monte.printer.printers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.printer.devices.OutputDevice;
import thkoeln.dungeon.monte.printer.finderservices.RobotFinderService;
import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.printer.util.MapCoordinate;
import thkoeln.dungeon.monte.printer.util.TwoDimDynamicArray;

import java.util.List;

/**
 * OutputDevice class to output the map of all planets and robots to console. The map usually contains of several
 * clusters, as we learn about planets bit by bit, and at first there are unconnected clustes, just like "islands".
 * Later, (hopefully), those clusters grow into one big continous map.
 *
 * Each map cluster is printed like this:
 *     |  0 |  1 |  2 |  3 |
 *     |----|----|----|----|
 *     |    |_2a3|    |    |
 *   0 |    |G 10|    |    |
 *     |    |S6ca|    |    |
 *     |----|----|----|----|
 *     |_753|    |_a45|_e21|
 *   1 |C 15|    |    |    |
 *     |    |    |    |    |
 *     |----|----|----|----|
 *     |    |#644|_d66|    |
 *   2 |    |    |    |    |
 *     |    |    |    |    |
 *     |----|----|----|----|
 *
 * Each cell of the map has three compartments.
 * - 1st (top) compartment is the planet name (_ for regular planet and # for spawnPoint, followed by the first
 *   three letters of the UUID)
 * - 2nd compartment is the resource (C 15 means 15000 units of coal)
 * - 3rd compartment is the robot (S/M/W for the type, followed by firstthree letters of the UUID)
 */
@Service
public class MapPrinter  {

    protected static final String EMPTY_COMPARTMENT = "    |";
    protected static final String SEPERATOR_COMPARTMENT = "----|";
    protected static final String SEPERATOR_CHAR = "|";

    private RobotFinderService robotFinderService;
    private PlanetPrinter planetPrinter;
    private List<OutputDevice> outputDevices;


    @Autowired
    public MapPrinter( RobotFinderService robotFinderService,
                       PlanetPrinter planetPrinter,
                       List<OutputDevice> outputDevices) {
        this.robotFinderService = robotFinderService;
        this.planetPrinter = planetPrinter;
        this.outputDevices = outputDevices;
    }


    /**
     * @return The map (or several cluster maps) of all known planets formatted for the console.
     *      This involves planets, but also robots located on planets. "planet" package doesn't know
     *      "robot" (but the other way around), so the best way to orchestrate this is from here.
     */
    public void printMap() {
        int currentClusterNumber = 0;
        List<TwoDimDynamicArray<PlanetPrintable>> allClusters = planetPrinter.allPlanetClusters();
        for ( TwoDimDynamicArray<PlanetPrintable> planetCluster : allClusters ) {
            currentClusterNumber += 1;
            final String headerString = "Planet cluster no. " + currentClusterNumber;
            outputDevices.forEach(p -> p.header( headerString ) );
            printMapCluster( planetCluster );
        }
    }


    /**
     * Print one cluster of the known map.
     * @param planetCluster
     * @return
     */
    private void printMapCluster( TwoDimDynamicArray<PlanetPrintable> planetCluster ) {
        MapCoordinate maxMapCoordinate = planetCluster.getMaxCoordinate();
        int maxColumns = maxMapCoordinate.getX() + 1;
        outputDevices.forEach(p -> p.startMap( maxColumns ) );

        TwoDimDynamicArray<MapCellPrintDto> printCellDtos = getPrintCellDtos( planetCluster );
        for (int y = 0; y <= maxMapCoordinate.getY(); y++ ) {
            final int rowNum = y;
            outputDevices.forEach(p -> p.startMapRow( rowNum, 3 ) );
            for ( int x = 0; x < maxColumns; x++ ) {
                String[] cellCompartments =  printCellDtos.at( x, y ).toCompartmentStrings();
                outputDevices.forEach(p -> p.writeCell( cellCompartments ) );
            }
            outputDevices.forEach(p -> p.endMapRow() );
        }
        outputDevices.forEach(p -> p.endMap() );
    }


    /**
     * Print one cluster of the known map.
     * @param planetCluster
     * @return
     */
    private TwoDimDynamicArray<MapCellPrintDto> getPrintCellDtos(TwoDimDynamicArray<PlanetPrintable> planetCluster ) {
        MapCoordinate maxMapCoordinate = planetCluster.getMaxCoordinate();
        TwoDimDynamicArray<MapCellPrintDto> printCellDtos = new TwoDimDynamicArray<>(maxMapCoordinate);
        for (int y = 0; y <= maxMapCoordinate.getY(); y++ ) {
            for (int x = 0; x <= maxMapCoordinate.getX(); x++ ) {
                PlanetPrintable planetPrintable = planetCluster.at( x, y );
                MapCellPrintDto mapPrintDto = new MapCellPrintDto( planetPrintable );
                mapPrintDto.setRobotPrintables( robotFinderService.livingRobotsOnPlanet( planetPrintable ) );
                printCellDtos.put(x, y, mapPrintDto);
            }
        }
        return printCellDtos;
    }

}
