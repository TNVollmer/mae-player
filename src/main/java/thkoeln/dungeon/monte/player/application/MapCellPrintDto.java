package thkoeln.dungeon.monte.player.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.planet.domain.Planet;
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
}
