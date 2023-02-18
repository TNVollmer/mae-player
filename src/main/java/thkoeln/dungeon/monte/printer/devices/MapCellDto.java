package thkoeln.dungeon.monte.printer.devices;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.printer.printables.RobotPrintable;
import thkoeln.dungeon.monte.printer.util.PrinterException;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for the printing of planets with the robots currently on them.
 */
@Getter
@Setter
@NoArgsConstructor
public class MapCellDto {
    private PlanetPrintable planetPrintable;
    private List<? extends RobotPrintable> robotPrintables = new ArrayList<>();

    public MapCellDto( PlanetPrintable planetPrintable ) {
        this.planetPrintable = planetPrintable;
    }

    public List<? extends RobotPrintable> ownRobots() {
        return robotPrintables.stream().filter( robot -> !robot.isEnemy() ).toList();
    }

    public List<? extends RobotPrintable> enemyRobots() {
        return robotPrintables.stream().filter( robot -> robot.isEnemy() ).toList();
    }

    public String[] toCompartmentStrings() {
        String[] cellCompartments = new String[]{ "", "", "" };

        cellCompartments[0] = ( planetPrintable == null ) ? "" : planetPrintable.mapName();
        cellCompartments[1] = robotMapString( ownRobots() );
        cellCompartments[2] = robotMapString( enemyRobots() );

        return cellCompartments;
    }


    private String robotMapString( List<? extends RobotPrintable> robots ) {
        if ( robots.size() == 0 ) return "";
        if ( robots.size() == 1 ) return robotPrintables.get( 0 ).toString();
        return "(" + robotPrintables.size() + ")";
    }


    public String cellCSSClass() {
        if ( planetPrintable != null && planetPrintable.isBlackHole() ) return "cell blackhole";
        if ( planetPrintable != null && !planetPrintable.hasBeenVisited() ) return "cell unvisited";
        return "cell";
    }

    public String innerCellCSSClass( int compartmentNumber ) {
        // mineable resource, denoted as a color coding
        if ( compartmentNumber == 0 && planetPrintable != null && planetPrintable.mineableResourcePrintable() != null ) {
            return "innercell ressource" + planetPrintable.mineableResourcePrintable().relativeValue();
        }
        // own robot
        if ( compartmentNumber == 1 && ownRobots().size() > 0 ) {
            return "innercell robot";
        }
        // enemy robot
        if ( compartmentNumber == 2 && enemyRobots().size() > 0 ) {
            return "innercell enemy" + enemyClassId( enemyRobots().get( 0 ) );
        }
        return "innercell"; // no resource or robot => no markup
    }


    private String enemyClassId( RobotPrintable enemy ) {
        if ( !enemy.isEnemy() ) throw new PrinterException( "!enemy.isEnemy()" );
        Character enemyChar = enemy.enemyChar();
        if ( enemyChar == null ) throw new PrinterException( "enemyChar == null" );
        if ( enemyChar > 'E' ) enemyChar = 'X';
        return String.valueOf( enemyChar );
    }

}
