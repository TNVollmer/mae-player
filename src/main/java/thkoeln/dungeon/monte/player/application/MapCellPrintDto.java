package thkoeln.dungeon.monte.player.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.player.domain.PlayerException;
import thkoeln.dungeon.monte.robot.domain.Robot;

import java.util.List;

/**
 * Used for the printing of planets with the robots currently on them.
 */
@Getter
@Setter
@NoArgsConstructor
public class MapCellPrintDto {
    private Planet planet;
    private List<Robot> robots;

    public MapCellPrintDto( Planet planet ) {
        this.planet = planet;
    }

    public String[] toCompartmentStrings() {
        String[] cellCompartments = new String[3];
        cellCompartments[0] = ( planet == null ) ? "" : planet.toString();
        cellCompartments[1] = ( planet == null || planet.getMineableResource() == null ) ?
                "" : planet.getMineableResource().toString();
        if ( robots == null || robots.size() == 0 ) cellCompartments[2] = "";
        else if ( robots.size() == 1 ) cellCompartments[2] = robots.get( 0 ).toString();
        else cellCompartments[2] = "(" + robots.size() + ")";

        return cellCompartments;
    }
}
