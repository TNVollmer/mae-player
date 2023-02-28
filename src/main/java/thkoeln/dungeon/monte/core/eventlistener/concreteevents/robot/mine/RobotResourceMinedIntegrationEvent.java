package thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.mine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.monte.core.domainprimitives.location.MineableResourceType;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.move.RobotMovePlanetDto;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotResourceMinedIntegrationEvent extends AbstractEvent {
    private UUID robotId;
    private int minedAmount = 0;
    private String minedResource;
    private RobotResourceInventoryDto resourceInventory;


    public MineableResource minedResourceAsDomainPrimitive() {
        MineableResource minedResourceAsDomainPrimitive = null;
        try {
            minedResourceAsDomainPrimitive = MineableResource.fromTypeAndAmount(
                    MineableResourceType.valueOf(minedResource.toUpperCase()), minedAmount);
        }
        catch ( Exception e ) {
            logger.debug( "Could not convert minedResource to MineableResource: " + e.getMessage() );
        }
        return minedResourceAsDomainPrimitive;
    }


    @Override
    public boolean isValid() {
        if ( robotId == null ) return false;
        if ( minedAmount <= 0 ) return false;
        if ( minedResource == null ) return false;
        if ( minedResourceAsDomainPrimitive() == null ) return false;
        return true;
    }
}
