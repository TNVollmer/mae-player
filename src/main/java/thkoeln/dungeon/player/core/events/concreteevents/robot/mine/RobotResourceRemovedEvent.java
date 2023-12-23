package thkoeln.dungeon.player.core.events.concreteevents.robot.mine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.player.core.events.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotResourceRemovedEvent extends AbstractEvent {
    private UUID robotId;
    private String removedResource;
    private int removedAmount = 0;
    private RobotResourceInventoryDto resourceInventory;


    public MineableResource removedResourceAsDomainPrimitive() {
        MineableResource minedResourceAsDomainPrimitive = null;
        try {
            minedResourceAsDomainPrimitive = MineableResource.fromTypeAndAmount(
                    MineableResourceType.valueOf(removedResource.toUpperCase()), removedAmount);
        }
        catch ( Exception e ) {
            logger.debug( "Could not convert removedResource to MineableResource: " + e.getMessage() );
        }
        return minedResourceAsDomainPrimitive;
    }


    @Override
    public boolean isValid() {
        if ( robotId == null ) return false;
        if ( removedAmount <= 0 ) return false;
        if ( removedResource == null ) return false;
        return removedResourceAsDomainPrimitive() != null;
    }
}
