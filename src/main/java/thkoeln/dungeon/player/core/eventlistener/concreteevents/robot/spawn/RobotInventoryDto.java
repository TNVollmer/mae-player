package thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.spawn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotInventoryDto {
    private Integer storageLevel = 0;
    private Integer usedStorage = 0;
    private RobotInventoryResourcesDto resources;
    private Boolean full = Boolean.FALSE;
    private Integer maxStorage;


    public boolean isValid() {
        if ( maxStorage == null ) return false;
        if ( maxStorage <= 0 ) return false;
        return true;
    }
}
