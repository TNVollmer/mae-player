package thkoeln.dungeon.monte.player.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter( AccessLevel.PROTECTED )
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Player {
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

    @Setter( AccessLevel.PUBLIC )
    private String playerExchange;


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
        resetToDefaultPlayerExchange();
    }


    public void resetToDefaultPlayerExchange() {
        if ( playerId == null ) return;
        this.playerExchange = "player-" + playerId;
    }


    public boolean isRegistered() {
        return getPlayerId() != null;
    }


    public boolean hasJoinedGame() {
        return getPlayerExchange() != null;
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
