package thkoeln.dungeon.player.core.events.concreteevents.robot.mine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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


    @Override
    public boolean isValid() {
        if ( robotId == null ) return false;
        if ( removedAmount <= 0 ) return false;
        if ( removedResource == null ) return false;
        return resourceInventory.isValid();
    }
}
