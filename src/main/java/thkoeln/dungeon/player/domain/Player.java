package thkoeln.dungeon.player.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.domainprimitives.Moneten;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.planet.domain.PlanetException;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Player {
    @Transient
    private Logger logger = LoggerFactory.getLogger( Player.class );

    @Id
    private final UUID id = UUID.randomUUID();

    @Setter
    private String name;
    @Setter
    private String email;
    @Setter( AccessLevel.PROTECTED )
    private UUID playerId;
    @Setter
    private String playerQueue;

    @Setter
    @Embedded
    private Moneten moneten = Moneten.fromInteger( 0 );

    private UUID registrationTransactionId;

    /**
     * Choose a random and unique name and email for the player
     */
    public void assignRandomName() {
        String randomNickname = NameGenerator.generateName();
        setName( randomNickname );
        setEmail( randomNickname + "@microservicedungeon.com" );
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


    public void playRound() {
        // todo
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
