package thkoeln.dungeon.monte.player.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

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

        // when / then
        player.setPlayerId( UUID.randomUUID() );
        assertTrue( player.isRegistered() );
        assertFalse( player.hasJoinedGame() );

        // when / then
        player.setPlayerExchange( "someString" );
        assertTrue( player.isRegistered() );
        assertTrue( player.hasJoinedGame() );
    }
}
