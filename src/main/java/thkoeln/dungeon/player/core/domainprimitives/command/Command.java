package thkoeln.dungeon.player.core.domainprimitives.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.DomainPrimitiveException;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.ItemType;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableType;

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
    @Column(name = "cmd_player_id")
    private UUID playerId;

    @Column(name = "cmd_type")
    @JsonProperty("type")
    private CommandType commandType;

    @Embedded
    @JsonProperty("data")
    private CommandObject commandObject;


    public static Command createMove( UUID robotId, UUID planetId, UUID gameId, UUID playerId ) {
        if ( robotId == null || planetId == null )
            throw new DomainPrimitiveException( "robotId == null || planetId == null" );
        Command command = new Command( CommandType.MOVEMENT, gameId, playerId );
        command.getCommandObject().setRobotId( robotId );
        command.getCommandObject().setPlanetId( planetId );
        return command;
    }


    public static Command createItemPurchase( ItemType item, int number, UUID robotId, UUID gameId, UUID playerId ) {
        if ( robotId == null || item == null || number < 0 )
            throw new DomainPrimitiveException( "Item purchase: robotId == null || item == null || number < 0" );
        if ( number == 0 ) return null;
        Command command = new Command( CommandType.BUYING, gameId, playerId );
        command.getCommandObject().setRobotId( robotId );
        command.getCommandObject().setItemQuantity( number );
        command.getCommandObject().setItemName( item.name() );
        return command;
    }


    public static Command createRobotPurchase( int number, UUID gameId, UUID playerId ) {
        if ( number < 0 ) throw new DomainPrimitiveException( "Robot purchase: number < 0" );
        if ( number < 1 ) return null;
        Command command = new Command( CommandType.BUYING, gameId, playerId );
        command.getCommandObject().setItemQuantity( number );
        command.getCommandObject().setItemName( TradeableType.ROBOT.toString() );
        return command;
    }

    public static Command createUpgrade( Capability capability, UUID robotId, UUID gameId, UUID playerId ) {
        if ( robotId == null || capability == null )
            throw new DomainPrimitiveException( "robotId == null || capability == null" );
        Command command = new Command( CommandType.BUYING, gameId, playerId );
        command.getCommandObject().setRobotId( robotId );
        command.getCommandObject().setItemName( capability.toStringForCommand() );
        return command;
    }


    public static Command createRegeneration( UUID robotId, UUID gameId, UUID playerId ) {
        if ( robotId == null ) throw new DomainPrimitiveException( "robotId == null " );
        Command command = new Command( CommandType.REGENERATE, gameId, playerId );
        command.getCommandObject().setRobotId( robotId );
        return command;
    }


    public static Command createMining( UUID robotId, UUID planetId, UUID gameId, UUID playerId ) {
        if ( robotId == null || planetId == null )
            throw new DomainPrimitiveException( "robotId == null || planetId == null" );
        Command command = new Command( CommandType.MINING, gameId, playerId );
        command.getCommandObject().setRobotId( robotId );
        command.getCommandObject().setPlanetId( planetId );
        return command;
    }


    public static Command createSelling(UUID robotId, UUID gameId, UUID playerId, MineableResource goods ) {
        if ( robotId == null ) throw new DomainPrimitiveException( "robotId == null " );
        Command command = new Command( CommandType.SELLING, gameId, playerId );
        command.getCommandObject().setRobotId( robotId );
        command.getCommandObject().setItemName( goods.getType().toString() );
        command.getCommandObject().setItemQuantity( goods.getAmount() );
        return command;
    }

    public static Command createFight(UUID robotId, UUID gameId, UUID playerId, UUID targetID) {
        if ( robotId == null || targetID == null )
            throw new DomainPrimitiveException( "robotId == null || targetId == null" );
        Command command = new Command( CommandType.BATTLE, gameId, playerId );
        command.getCommandObject().setRobotId( robotId );
        command.getCommandObject().setTargetId( targetID );
        return command;
    }

    protected Command( CommandType type, UUID gameId, UUID playerId ) {
        if ( gameId == null || playerId == null || type == null )
            throw new DomainPrimitiveException( "gameId == null || playerId == null || type == null" );
        setCommandObject( new CommandObject() );
        setCommandType( type );
        setPlayerId( playerId );
    }


    public boolean isRobotPurchase() {
        if ( commandObject == null || commandObject.getItemName() == null ) return false;
        return ( commandType.equals( CommandType.BUYING ) &&
                 commandObject.getItemName().equals( TradeableType.ROBOT.toString() ) );
    }


    @Override
    public String toString() {
        String printString = commandType.name();
        if ( commandObject != null && commandObject.getRobotId() != null )
            printString += " R:" + commandObject.getRobotId().toString().substring( 0, 3 );
        if ( commandObject != null && commandObject.getPlanetId() != null )
            printString += " P:" + commandObject.getPlanetId().toString().substring( 0, 3 );
        if ( commandObject != null && commandObject.getItemName() != null )
            printString += " I:" + commandObject.getItemName();
        return printString;
    }
}
