package thkoeln.dungeon.monte.core.domainprimitives.command;

import lombok.*;

import javax.persistence.Embeddable;
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
    private CommandType commandType;
    private UUID planetId;
    private UUID targetId;
    private String itemName;
    private Integer itemQuantity;
}
