package thkoeln.dungeon.player.player.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {
    @Transient
    private Logger logger = LoggerFactory.getLogger( Player.class );

    @Id
    private final UUID id = UUID.randomUUID();

    // GameId is stored for convenience - you need this for creating commands.
    @Setter(AccessLevel.PUBLIC)
    private UUID gameId;

    private String name;
    private String email;
    private UUID playerId;

    @Embedded
    private Money bankAccount = Money.zero();
    @Setter(AccessLevel.PUBLIC)
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "upgrade_amount"))
    private Money upgradeBudget = Money.zero();
    @Setter(AccessLevel.PUBLIC)
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "robot_amount"))
    private Money newRobotsBudget = Money.zero();


    @Setter(AccessLevel.PUBLIC)
    private String playerExchange;

    @Setter(AccessLevel.PUBLIC)
    private String playerQueue;

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

    public void initBank(Integer balance) {
        Money money = Money.from(balance);
        bankAccount = bankAccount.increaseBy(money);
        newRobotsBudget = newRobotsBudget.increaseBy(money);
    }

    public void depositInBank(Integer balance) {
        bankAccount = bankAccount.increaseBy(Money.from(balance));

        Integer split = balance / 3;

        upgradeBudget = upgradeBudget.increaseBy(Money.from(split));
        newRobotsBudget = newRobotsBudget.increaseBy(Money.from(split));
    }

    public void withdrawFromBank(Integer balance) {
        bankAccount = bankAccount.decreaseBy(Money.from(balance * -1));
    }

    @Override
    public String toString() {
        return "Player '" + name + "' (email: " + getEmail() + ", playerId: " + playerId + ")" + "Owns following robots: " + "List is brocken: could not initialize proxy - no Session";
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        Player player = (Player) o;
        return Objects.equals( id, player.id ) && Objects.equals( gameId, player.gameId )
                && Objects.equals( name, player.name ) && Objects.equals( email,
                player.email ) && Objects.equals( playerId, player.playerId )
                && Objects.equals( playerExchange, player.playerExchange );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, gameId, name, email, playerId, playerExchange );
    }

}
