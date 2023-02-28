package thkoeln.dungeon.monte.player.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.strategy.AccountInformation;
import thkoeln.dungeon.monte.core.strategy.Actionable;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.trading.TradeablePricesEvent;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter( AccessLevel.PROTECTED )
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Player implements ActionablePlayer, Actionable {
    @Transient
    private Logger logger = LoggerFactory.getLogger( Player.class );

    // What share of the available credit balance should be re-invested into new robots?
    // Could be moved to a player strategy class, but there is currently not yet enough "beef"
    // for such a strategy class.
    public final static float SHARE_OF_CREDIT_BALANCE_FOR_NEW_ROBOTS = 1.0f;

    @Id
    private final UUID id = UUID.randomUUID();

    // GameId is stored for convenience - you need this for creating commands.
    @Setter( AccessLevel.PUBLIC )
    private UUID gameId;

    private String name;
    private String email;
    private UUID playerId;
    @Getter( AccessLevel.NONE )
    private String enemyShortName = null;

    // Each enemy player is described by a letter that is used for visualizing in the client
    @Setter( AccessLevel.PUBLIC )
    private Character enemyChar;
    private Command recentCommand;

    @Transient
    @Setter( AccessLevel.PUBLIC )
    PlayerStrategy strategy;

    @Setter( AccessLevel.PUBLIC )
    private String playerQueue;


    public static Player ownPlayer( String name, String email ) {
        Player player = new Player();
        player.setName( name );
        player.setEmail( email );
        return player;
    }


    public static Player enemyPlayer( String shortName ) {
        if ( shortName == null ) throw new PlayerException( "shortName == null" );
        Player player = new Player();
        player.setEnemyShortName( shortName );
        return player;
    }



    public void assignPlayerId( UUID playerId ) {
        if ( playerId == null ) throw new PlayerException( "playerId == null" );
        this.playerId = playerId;

        // this we do in order to register the queue early - before joining the game
        resetToDefaultPlayerQueue();
    }


    public void resetToDefaultPlayerQueue() {
        if ( playerId == null ) return;
        this.playerQueue = "player-" + playerId;
    }


    public boolean isRegistered() {
        return getPlayerId() != null;
    }


    public boolean hasJoinedGame() {
        return getPlayerQueue() != null;
    }


    public boolean isEnemy() {
        if ( playerId == null && enemyShortName != null ) return true;
        return false;
    }

    public boolean matchesShortName( String shortName ) {
        if ( shortName == null ) return false;
        if ( enemyShortName != null && enemyShortName.equals( shortName) ) return true;
        if ( playerId != null && enemyShortName == null && playerId.toString().substring( 0, 8 ).equals( shortName ) )
            return true;
        return false;
    }


    @Override
    public Command decideNextCommand( AccountInformation accountInformation ) {
        Command nextCommand = null;
        if ( strategy == null ) {
            logger.error( "No strategy set for Player! Can't decide on a command." );
        }
        else {
            nextCommand = strategy.findNextCommand( this, accountInformation );
            logger.info( "Decided on command " + nextCommand + " for player." );
        }
        setRecentCommand( nextCommand );
        return nextCommand;
    }


    @Override
    public Command buyRobots( AccountInformation accountInformation ) {
        int numOfNewRobots = accountInformation.canBuyThatManyRobotsWith( SHARE_OF_CREDIT_BALANCE_FOR_NEW_ROBOTS );
        logger.info( "Can buy " + numOfNewRobots + " robots ..." );
        Command command = Command.createRobotPurchase( numOfNewRobots, getGameId(), getPlayerId() );
        return command;
    }


    private void handleTradablePricesEvent( TradeablePricesEvent event ) {
        logger.info( "TradeablePricesEvent - no handling at the moment, assume prices to be fix." );
    }


    @Override
    public String toString() {
        return "Player '" + name + "' (email: " + getEmail() + ", playerId: " + playerId + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
