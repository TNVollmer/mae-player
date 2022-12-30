package thkoeln.dungeon.monte.player.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.domainprimitives.Coordinate;
import thkoeln.dungeon.monte.domainprimitives.TwoDimDynamicArray;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.application.PlanetPrinter;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.player.domain.PlayerException;
import thkoeln.dungeon.monte.robot.application.RobotApplicationService;
import thkoeln.dungeon.monte.robot.domain.Robot;
import thkoeln.dungeon.monte.util.AbstractPrinter;

import java.util.List;
import java.util.Map;

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
public class MapPrinter extends AbstractPrinter {
    protected static final String EMPTY_COMPARTMENT = "    |";
    protected static final String SEPERATOR_COMPARTMENT = "----|";
    protected static final String SEPERATOR_CHAR = "|";

    private RobotApplicationService robotApplicationService;
    private PlanetPrinter planetPrinter;

    @Autowired
    public MapPrinter( RobotApplicationService robotApplicationService,
                       PlanetPrinter planetPrinter ) {
        this.robotApplicationService = robotApplicationService;
        this.planetPrinter = planetPrinter;
    }


    /**
     * @return The map (or several cluster maps) of all known planets formatted for the console.
     *      This involves planets, but also robots located on planets. "planet" package doesn't know
     *      "robot" (but the other way around), so the best way to orchestrate this is from here.
     */
    public void printMap() {
        writeLine();
        int currentClusterNumber = 0;
        List<TwoDimDynamicArray<Planet>> allClusters = planetPrinter.allPlanetClusters();
        for ( TwoDimDynamicArray<Planet> planetCluster : allClusters ) {
            currentClusterNumber += 1;
            writeLine( "Planet cluster no. " + currentClusterNumber + ":" );
            printMapCluster( planetCluster );
            writeLine();
        }
    }


    /**
     * Print one cluster of the known map.
     * @param planetCluster
     * @return
     */
    private void printMapCluster( TwoDimDynamicArray<Planet> planetCluster ) {
        Coordinate maxCoordinate = planetCluster.getMaxCoordinate();
        printTopRow( maxCoordinate );

        TwoDimDynamicArray<MapCellPrintDto> printCellDtos = getPrintCellDtos( planetCluster );
        for ( int y = 0; y <= maxCoordinate.getY(); y++ ) {
            for ( int compartmentNumber = 1; compartmentNumber <= 4; compartmentNumber++ ) {
                printRowNumberOrBlanks( y, compartmentNumber );
                for ( int x = 0; x <= maxCoordinate.getX(); x++ ) {
                    printCell( printCellDtos.at( x, y ), compartmentNumber );
                }
                writeLine();
            }
        }
    }



    /**
     * Print the top row of the map cluster with coordinate numbers.
     * @param maxClusterPoint
     * @return
     */
    public void printTopRow( Coordinate maxClusterPoint ) {
        write( EMPTY_COMPARTMENT );
        for ( int columnNumber = 0; columnNumber <= maxClusterPoint.getX(); columnNumber++ ) {
            write( String.format( "%1$3s", columnNumber ) + " " + SEPERATOR_CHAR );
        }
        writeLine();
        write( EMPTY_COMPARTMENT );
        for ( int columnNumber = 0; columnNumber <= maxClusterPoint.getX(); columnNumber++ ) {
            write( SEPERATOR_COMPARTMENT );
        }
        writeLine();
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


    private void printRowNumberOrBlanks( int rowNumber, int compartmentNumber ) {
        if ( compartmentNumber == 2 ) {
            write( String.format( "%1$3s", rowNumber ) + " " + SEPERATOR_CHAR );
        }
        else {
            write( EMPTY_COMPARTMENT );
        }
    }



    private void printCell( MapCellPrintDto printCellDto, int compartmentNumber ) {
        if ( compartmentNumber < 1 || compartmentNumber > 4 )
            throw new PlayerException( "compartmentNumber < 1 || compartmentNumber > 4" );
        Planet planet = printCellDto.getPlanet();
        switch ( compartmentNumber ) {
            case 1:
                if ( planet == null ) write( EMPTY_COMPARTMENT );
                else write( planet.toString() + SEPERATOR_CHAR );
                break;
            case 2:
                if ( planet == null || planet.getMineableResource() == null ) write( EMPTY_COMPARTMENT );
                else write( planet.getMineableResource().toString() + SEPERATOR_CHAR );
                break;
            case 3:
                printRobotCompartment( printCellDto );
                break;
            default: write( SEPERATOR_COMPARTMENT );
        }
    }


    private void printRobotCompartment( MapCellPrintDto printCellDto ) {
        List<Robot> robotsOnPlanet = printCellDto.getRobots();
        if ( robotsOnPlanet == null || robotsOnPlanet.size() == 0 ) {
            write( EMPTY_COMPARTMENT );
        }
        else if ( robotsOnPlanet.size() == 1 ) {
            write( robotsOnPlanet.get( 0 ).toString() + SEPERATOR_CHAR );
        }
        else {
            write( " (" + robotsOnPlanet.size() + ")" + SEPERATOR_CHAR );
        }
    }

}
