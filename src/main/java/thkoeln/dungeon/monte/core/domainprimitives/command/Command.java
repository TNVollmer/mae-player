package thkoeln.dungeon.monte.core.domainprimitives.command;

import lombok.*;
import thkoeln.dungeon.monte.core.domainprimitives.DomainPrimitiveException;

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
public class Command {
    private UUID gameId;
    private UUID playerId;
    private UUID robotId;
    private CommandType commandType;

    @Embedded
    private CommandObject commandObject;

    public static Command ofType( CommandType commandType ) {
        if ( commandType == null ) throw new DomainPrimitiveException( "commandType == null" );
        Command command = new Command();
        command.setCommandType( commandType );
        return command;
    }

    @Override
    public String toString() {
        return "Command{" +
                "robotId=" + robotId +
                ", commandType=" + commandType + "\n\t" +
                ", commandObject=" + commandObject +
                '}';
    }
}
