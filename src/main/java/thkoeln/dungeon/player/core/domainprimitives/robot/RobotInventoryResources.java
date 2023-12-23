package thkoeln.dungeon.player.core.domainprimitives.robot;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Embeddable;
import lombok.*;
import thkoeln.dungeon.player.core.events.concreteevents.robot.spawn.RobotInventoryResourcesDto;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Embeddable
public class RobotInventoryResources {
    private Integer coal;
    private Integer iron;
    private Integer gem;
    private Integer gold;
    private Integer platin;

    public static RobotInventoryResources empty() {
        return new RobotInventoryResources(0, 0, 0, 0, 0);
    }

    public RobotInventoryResources updateResources(RobotInventoryResourcesDto robotInventoryResourcesDto) {
        this.coal = robotInventoryResourcesDto.getCoal();
        ;
        this.iron = robotInventoryResourcesDto.getIron();
        this.gem = robotInventoryResourcesDto.getGem();
        this.gold = robotInventoryResourcesDto.getGold();
        this.platin = robotInventoryResourcesDto.getPlatin();
        return this;
    }

    @Override
    public String toString() {
        return "RobotInventoryResources{" +
                "coal=" + coal +
                ", iron=" + iron +
                ", gem=" + gem +
                ", gold=" + gold +
                ", platin=" + platin +
                '}';
    }
}
