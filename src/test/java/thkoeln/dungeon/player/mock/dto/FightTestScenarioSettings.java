package thkoeln.dungeon.player.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FightTestScenarioSettings extends TestScenarioSettings {

    private Integer mapSize;
    private List<PlanetConfigDto> planets;

    private PlayerConfigDto player;
    private PlayerConfigDto enemy;

    private List<FriendlyRobotDto> friendlyRobots;
    private List<EnemyRobotDto> enemyRobots;

}
