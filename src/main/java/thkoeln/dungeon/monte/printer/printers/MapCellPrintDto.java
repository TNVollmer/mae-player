package thkoeln.dungeon.monte.printer.printers;

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
public class MapCellPrintDto {
    private PlanetPrintable planetPrintable;
    private List<? extends RobotPrintable> robotPrintables;

    public MapCellPrintDto( PlanetPrintable planetPrintable) {
        this.planetPrintable = planetPrintable;
    }

    public String[] toCompartmentStrings() {
        String[] cellCompartments = new String[3];
        cellCompartments[0] = ( planetPrintable == null ) ? "" : planetPrintable.mapName();
        cellCompartments[1] = ( planetPrintable == null || planetPrintable.mineableResourcePrintable() == null ) ?
                "" : planetPrintable.mineableResourcePrintable().mapName();
        if ( robotPrintables == null || robotPrintables.size() == 0 ) cellCompartments[2] = "";
        else if ( robotPrintables.size() == 1 ) cellCompartments[2] = robotPrintables.get( 0 ).toString();
        else cellCompartments[2] = "(" + robotPrintables.size() + ")";

        return cellCompartments;
    }
}
