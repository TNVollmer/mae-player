package thkoeln.dungeon.monte.player.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.location.Coordinate;
import thkoeln.dungeon.monte.core.util.Printer;
import thkoeln.dungeon.monte.core.util.TwoDimDynamicArray;
import thkoeln.dungeon.monte.planet.application.PlanetPrinter;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.player.domain.PlayerException;
import thkoeln.dungeon.monte.robot.application.RobotApplicationService;
import thkoeln.dungeon.monte.robot.domain.Robot;
import thkoeln.dungeon.monte.core.util.ConsolePrinter;

import java.util.List;

/**
 * Printer class to output the map of all planets and robots to console. The map usually contains of several
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
 * - 1st (top) compartment is the planet name (_ for regular planet and # for spacestation, followed by the first
 *   three letters of the UUID)
 * - 2nd compartment is the resource (C 15 means 15000 units of coal)
 * - 3rd compartment is the robot (S/M/W for the type, followed by firstthree letters of the UUID)
 */
@Service
public class MapPrinter  {

    protected static final String EMPTY_COMPARTMENT = "    |";
    protected static final String SEPERATOR_COMPARTMENT = "----|";
    protected static final String SEPERATOR_CHAR = "|";

    private RobotApplicationService robotApplicationService;
    private PlanetPrinter planetPrinter;
    private List<Printer> printers;


    @Autowired
    public MapPrinter( RobotApplicationService robotApplicationService,
                       PlanetPrinter planetPrinter,
                       List<Printer> printers ) {
        this.robotApplicationService = robotApplicationService;
        this.planetPrinter = planetPrinter;
        this.printers = printers;
    }


    /**
     * @return The map (or several cluster maps) of all known planets formatted for the console.
     *      This involves planets, but also robots located on planets. "planet" package doesn't know
     *      "robot" (but the other way around), so the best way to orchestrate this is from here.
     */
    public void printMap() {
        int currentClusterNumber = 0;
        List<TwoDimDynamicArray<Planet>> allClusters = planetPrinter.allPlanetClusters();
        for ( TwoDimDynamicArray<Planet> planetCluster : allClusters ) {
            currentClusterNumber += 1;
            final String headerString = "Planet cluster no. " + currentClusterNumber;
            printers.forEach( p -> p.header( headerString ) );
            printMapCluster( planetCluster );
        }
    }


    /**
     * Print one cluster of the known map.
     * @param planetCluster
     * @return
     */
    private void printMapCluster( TwoDimDynamicArray<Planet> planetCluster ) {
        Coordinate maxCoordinate = planetCluster.getMaxCoordinate();
        int maxColumns = maxCoordinate.getX() + 1;
        printers.forEach( p -> p.startTable( maxColumns ) );

        TwoDimDynamicArray<MapCellPrintDto> printCellDtos = getPrintCellDtos( planetCluster );
        for ( int y = 0; y <= maxCoordinate.getY(); y++ ) {
            final int rowNum = y;
            printers.forEach( p -> p.startRow( rowNum, 3 ) );
            for ( int x = 0; x < maxColumns; x++ ) {
                String[] cellCompartments =  printCellDtos.at( x, y ).toCompartmentStrings();
                printers.forEach( p -> p.writeCell( cellCompartments ) );
            }
            printers.forEach( p -> p.endRow() );
        }
        printers.forEach( p -> p.endTable() );
    }


    /**
     * Print one cluster of the known map.
     * @param planetCluster
     * @return
     */
    private TwoDimDynamicArray<MapCellPrintDto> getPrintCellDtos(TwoDimDynamicArray<Planet> planetCluster ) {
        Coordinate maxCoordinate = planetCluster.getMaxCoordinate();
        TwoDimDynamicArray<MapCellPrintDto> printCellDtos = new TwoDimDynamicArray<>( maxCoordinate );
        for ( int y = 0; y <= maxCoordinate.getY(); y++ ) {
            for ( int x = 0; x <= maxCoordinate.getX(); x++ ) {
                Planet planet = planetCluster.at( x, y );
                MapCellPrintDto mapPrintDto = new MapCellPrintDto( planet );
                mapPrintDto.setRobots( robotApplicationService.livingRobotsOnPlanet( planet ) );
                printCellDtos.put(x, y, mapPrintDto);
            }
        }
        return printCellDtos;
    }

}
