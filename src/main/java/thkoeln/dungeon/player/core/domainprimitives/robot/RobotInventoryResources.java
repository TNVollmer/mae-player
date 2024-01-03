package thkoeln.dungeon.player.core.domainprimitives.robot;


import jakarta.persistence.Embeddable;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Embeddable
public class RobotInventoryResources {
    //ERROR from Springboot in mapping when using MineableResource-DomainPrimitive, resorting back to Integers
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
            default:
                throw new IllegalStateException("Unexpected resource-type value: " + mineableResource.getType());
        }
    }

    public int getUsedStorage() {
        return coal + iron + gem + gold + platin;
    }

    public MineableResource getHighestMinedResource() {
        if (coal > 0) {
            return MineableResource.fromTypeAndAmount(MineableResourceType.COAL, coal);
        } else if (iron > 0) {
            return MineableResource.fromTypeAndAmount(MineableResourceType.IRON, iron);
        } else if (gem > 0) {
            return MineableResource.fromTypeAndAmount(MineableResourceType.GEM, gem);
        } else if (gold > 0) {
            return MineableResource.fromTypeAndAmount(MineableResourceType.GOLD, gold);
        } else if (platin > 0) {
            return MineableResource.fromTypeAndAmount(MineableResourceType.PLATIN, platin);
        } else {
            return null;
        }
    }

    public void removeResource(MineableResource mineableResource) {
        switch (mineableResource.getType()) {
            case COAL:
                this.coal -= mineableResource.getAmount();
                break;
            case IRON:
                this.iron -= mineableResource.getAmount();
                break;
            case GEM:
                this.gem -= mineableResource.getAmount();
                break;
            case GOLD:
                this.gold -= mineableResource.getAmount();
                break;
            case PLATIN:
                this.platin -= mineableResource.getAmount();
                break;
            default:
                throw new IllegalStateException("Unexpected resource-type value: " + mineableResource.getType());
        }
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
