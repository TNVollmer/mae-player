package thkoeln.dungeon.monte.printer.devices;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.printer.printables.PlanetPrintable;
import thkoeln.dungeon.monte.printer.printables.RobotPrintable;

import java.util.List;

/**
 * Used for the printing of planets with the robots currently on them.
 */
@Getter
@Setter
@NoArgsConstructor
public class MapCellDto {
    private PlanetPrintable planetPrintable;
    private List<? extends RobotPrintable> robotPrintables;

    public MapCellDto(PlanetPrintable planetPrintable) {
        this.planetPrintable = planetPrintable;
    }

    public String[] toCompartmentStrings() {
        String[] cellCompartments = new String[3];
        cellCompartments[0] = ( planetPrintable == null ) ? "" : planetPrintable.mapName();
        // todo change to enemy robot here
        cellCompartments[1] = ( planetPrintable == null || planetPrintable.mineableResourcePrintable() == null ) ?
                "" : planetPrintable.mineableResourcePrintable().mapName();
        if ( robotPrintables == null || robotPrintables.size() == 0 ) cellCompartments[2] = "";
        else if ( robotPrintables.size() == 1 ) cellCompartments[2] = robotPrintables.get( 0 ).toString();
        else cellCompartments[2] = "(" + robotPrintables.size() + ")";

        return cellCompartments;
    }


    public String cellCSSClass() {
        if ( planetPrintable != null && planetPrintable.isBlackHole() ) return "cell blackhole";
        if ( planetPrintable != null && !planetPrintable.hasBeenVisited() ) return "cell unvisited";
        return "cell";
    }

    public String innerCellCSSClass( int compartmentNumber ) {
        // mineable resource
        if ( compartmentNumber == 0 && planetPrintable != null && planetPrintable.mineableResourcePrintable() != null ) {
            return "innercell ressource" + planetPrintable.mineableResourcePrintable().relativeValue();
        }
        // robot
        if ( compartmentNumber == 2 && robotPrintables.size() > 0) {
            return "innercell robot";
        }
        return "innercell"; // no resource or robot => no markup
    }

}
