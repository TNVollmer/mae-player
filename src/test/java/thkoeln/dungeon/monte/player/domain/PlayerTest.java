package thkoeln.dungeon.monte.player.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;
import thkoeln.dungeon.monte.player.domain.Player;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    @Value("${dungeon.playerName}")
    protected String playerName;
    @Value("${dungeon.playerEmail}")
    protected String playerEmail;

    @Test
    public void testStatus() {
        // given
        Player player = Player.ownPlayer( playerName, playerEmail );
        assertFalse( player.isRegistered() );
        assertFalse( player.hasJoinedGame() );
        assertFalse( player.isEnemy() );

        // when / then
        player.setPlayerId( UUID.randomUUID() );
        assertTrue( player.isRegistered() );
        assertFalse( player.hasJoinedGame() );
        assertFalse( player.isEnemy() );

        // when / then
        player.setPlayerQueue( "someString" );
        assertTrue( player.isRegistered() );
        assertTrue( player.hasJoinedGame() );
        assertFalse( player.isEnemy() );
    }

    @Test
    public void testEnemyMatch() {
        // given
        Player player = Player.enemyPlayer( "abcd1234" );

        // when / then
        assertTrue( player.isEnemy() );
        assertTrue( player.matchesShortName( "abcd1234" ) );
    }


    @Test
    public void testNonEnemyMatch() {
        // given
        Player player = Player.ownPlayer( playerName, playerEmail );
        UUID playerId = UUID.randomUUID();
        String shortName = playerId.toString().substring( 0, 8 );

        // when
        assertFalse( player.isEnemy() );
        player.assignPlayerId( playerId );

        // then
        assertFalse( player.isEnemy() );
        assertTrue( player.matchesShortName( shortName ) );
    }
}
