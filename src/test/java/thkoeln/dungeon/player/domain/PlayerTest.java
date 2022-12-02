package thkoeln.dungeon.player.domain;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.domainprimitives.CompassDirection;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;

import javax.transaction.Transactional;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    @Value("${dungeon.playerName}")
    protected String playerName;
    @Value("${dungeon.playerEmail}")
    protected String playerEmail;

    @Test
    public void testStatus() {
        // given
        Player player = new Player();
        player.setName( playerName );
        player.setEmail( playerEmail );
        Assert.assertFalse( player.isRegistered() );
        Assert.assertFalse( player.hasJoinedGame() );

        // when / then
        player.setPlayerId( UUID.randomUUID() );
        Assert.assertTrue( player.isRegistered() );
        Assert.assertFalse( player.hasJoinedGame() );

        // when / then
        player.setPlayerQueue( "someString" );
        Assert.assertTrue( player.isRegistered() );
        Assert.assertTrue( player.hasJoinedGame() );

    }
}
