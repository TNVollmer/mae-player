package thkoeln.dungeon.monte.core.domainprimitives.command;

import lombok.*;
import thkoeln.dungeon.monte.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.ItemType;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.TradeableType;
import thkoeln.dungeon.monte.robot.domain.RobotType;

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


    public static Command createMove( UUID robotId, UUID planetId, UUID gameId, UUID playerId ) {
        if ( robotId == null || planetId == null )
            throw new DomainPrimitiveException( "robotId == null || planetId == null" );
        Command command = new Command( CommandType.MOVEMENT, gameId, playerId );
        command.setRobotId( robotId );
        command.getCommandObject().setPlanetId( planetId );
        return command;
    }


    public static Command createItemPurchase( ItemType item, int number, UUID robotId, UUID gameId, UUID playerId ) {
        if ( robotId == null || item == null  )
            throw new DomainPrimitiveException( "Item purchase: robotId == null || item == null" );
        if ( number < 1 ) return null;
        Command command = new Command( CommandType.BUYING, gameId, playerId );
        command.setRobotId( robotId );
        command.getCommandObject().setItemQuantity( number );
        command.getCommandObject().setItemName( item.name() );
        return command;
    }


    public static Command createRobotPurchase( int number, UUID gameId, UUID playerId ) {
        if ( number < 1 ) return null;
        Command command = new Command( CommandType.BUYING, gameId, playerId );
        command.getCommandObject().setItemQuantity( number );
        command.getCommandObject().setItemName( TradeableType.ROBOT.name() );
        return command;
    }

    public static Command createUpgrade( Capability capability, UUID robotId, UUID gameId, UUID playerId ) {
        if ( robotId == null || capability == null )
            throw new DomainPrimitiveException( "robotId == null || capability == null" );
        Command command = new Command( CommandType.BUYING, gameId, playerId );
        command.setRobotId( robotId );
        command.getCommandObject().setItemName( capability.toStringForCommand() );
        return command;
    }


    public static Command createRegeneration( UUID robotId, UUID gameId, UUID playerId ) {
        if ( robotId == null ) throw new DomainPrimitiveException( "robotId == null " );
        Command command = new Command( CommandType.REGENERATE, gameId, playerId );
        command.setRobotId( robotId );
        return command;
    }


    protected Command( CommandType type, UUID gameId, UUID playerId ) {
        if ( gameId == null || playerId == null || type == null )
            throw new DomainPrimitiveException( "gameId == null || playerId == null || type == null" );
        setCommandObject( new CommandObject() );
        setCommandType( type );
        getCommandObject().setCommandType( type );
        setPlayerId( playerId );
        setGameId( gameId );
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
