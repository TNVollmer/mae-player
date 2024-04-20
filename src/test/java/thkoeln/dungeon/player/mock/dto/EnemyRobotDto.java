package thkoeln.dungeon.player.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnemyRobotDto {

    private UUID id;

    private PlanetConfigDto planet;

    private Integer healthLevel;
    private Integer energyLevel;
    private Integer energyRegenLevel;
    private Integer damageLevel;
    private Integer miningLevel;
    private Integer miningSpeedLevel;

    private Integer health;
    private Integer energy;

    private List<NextOrder> nextOrders;

}
