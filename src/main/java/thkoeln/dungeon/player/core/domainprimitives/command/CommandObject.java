package thkoeln.dungeon.player.core.domainprimitives.command;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

/**
 * Domain Primitive to represent a command object
 */
@Embeddable
@EqualsAndHashCode
@Getter
@Setter( AccessLevel.PROTECTED )
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommandObject {
    @Column(name = "cmd_robot_id")
    private UUID robotId;
    @Column(name = "cmd_planet_id")
    private UUID planetId;
    @Column(name = "cmd_target_id")
    private UUID targetId;
    @Column(name = "cmd_item_name")
    private String itemName;
    @Column(name = "cmd_item_quantity")
    private Integer itemQuantity;
}
