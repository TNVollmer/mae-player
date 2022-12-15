package thkoeln.dungeon.domainprimitives;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.UUID;

/**
 * Domain Primitive to represent a command that a player can send
 */
@Embeddable
@EqualsAndHashCode
@Getter
@Setter( AccessLevel.PROTECTED )
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Command {
    private UUID gameId;
    private UUID playerId;
    private UUID robotId;
    private CommandType commandType;

    @Embedded
    private CommandObject commandObject;
}
