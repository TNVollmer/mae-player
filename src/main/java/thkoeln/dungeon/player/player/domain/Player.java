package thkoeln.dungeon.player.player.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter( AccessLevel.PROTECTED )
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Player {
    @Transient
    private Logger logger = LoggerFactory.getLogger( Player.class );

    @Id
    private final UUID id = UUID.randomUUID();

    // GameId is stored for convenience - you need this for creating commands.
    @Setter( AccessLevel.PUBLIC )
    private UUID gameId;

    private String name;
    private String email;
    private UUID playerId;

    @Setter( AccessLevel.PUBLIC )
    private String playerExchange;

    public static Player ownPlayer( String name, String email ) {
        Player player = new Player();
        player.setName( name );
        player.setEmail( email );
        return player;
    }

    public void assignPlayerId( UUID playerId ) {
        if ( playerId == null ) throw new PlayerException( "playerId == null" );
        this.playerId = playerId;

        // this we do in order to register the queue early - before joining the game
        resetToDefaultPlayerExchange();
    }


    public void resetToDefaultPlayerExchange() {
        if ( name == null ) throw new PlayerException( "name == null" );
        this.playerExchange = "player-" + name;
    }


    public boolean isRegistered() {
        return getPlayerId() != null;
    }


    public boolean hasJoinedGame() {
        return getPlayerExchange() != null;
    }

    @Override
    public String toString() {
        return "Player '" + name + "' (email: " + getEmail() + ", playerId: " + playerId + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return Objects.equals(id, player.id) && Objects.equals(gameId, player.gameId)
            && Objects.equals(name, player.name) && Objects.equals(email,
            player.email) && Objects.equals(playerId, player.playerId)
            && Objects.equals(playerExchange, player.playerExchange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gameId, name, email, playerId, playerExchange);
    }
}
