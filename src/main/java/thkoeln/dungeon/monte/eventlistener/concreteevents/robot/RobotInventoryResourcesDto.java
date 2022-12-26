package thkoeln.dungeon.monte.eventlistener.concreteevents.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
public class RobotInventoryResourcesDto {
    private Integer coal = 0;
    private Integer iron = 0;
    private Integer gem = 0;
    private Integer gold = 0;
    private Integer platin = 0;
}
