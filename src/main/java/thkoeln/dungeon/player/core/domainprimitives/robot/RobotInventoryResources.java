package thkoeln.dungeon.player.core.domainprimitives.robot;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
public class RobotInventoryResources {
    private Integer coal;
    private Integer iron;
    private Integer gem;
    private Integer gold;
    private Integer platin;

    public static RobotInventoryResources empty() {
        return new RobotInventoryResources(0, 0, 0, 0, 0);
    }

    public RobotInventoryResources add(RobotInventoryResources resources) {
        if (resources == null) throw new IllegalArgumentException("resources cannot be null!");
        return new RobotInventoryResources(
                this.coal + resources.coal,
                this.iron + resources.iron,
                this.gem + resources.gem,
                this.gold + resources.gold,
                this.platin + resources.platin
        );
    }
}
