package thkoeln.dungeon.player.core.domainprimitives.robot;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Embeddable;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
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

    public void updateResource(MineableResource mineableResource) {
        switch (mineableResource.getType()) {
            case COAL:
                this.coal += mineableResource.getAmount();
                break;
            case IRON:
                this.iron += mineableResource.getAmount();
                break;
            case GEM:
                this.gem += mineableResource.getAmount();
                break;
            case GOLD:
                this.gold += mineableResource.getAmount();
                break;
            case PLATIN:
                this.platin += mineableResource.getAmount();
                break;
        }
    }

    public int getUsedStorage() {
        return coal + iron + gem + gold + platin;
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
