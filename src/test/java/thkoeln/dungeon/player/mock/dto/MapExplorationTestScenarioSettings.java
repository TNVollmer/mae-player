package thkoeln.dungeon.player.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MapExplorationTestScenarioSettings extends TestScenarioSettings {

    private Integer mapSize;
    private Boolean plentiful;
    private Integer explorerAmount;

}
