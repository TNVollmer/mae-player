package thkoeln.dungeon.player.application;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.core.AbstractDungeonMockingTest;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static thkoeln.dungeon.game.domain.GameStatus.*;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlayerBearerTokenTest extends AbstractDungeonMockingTest {
    @Autowired
    private Environment env;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerApplicationService playerApplicationService;
    private Game game;

    @BeforeClass
    public static void beforeAll() {
    }

    @AfterClass
    public static void afterAll() {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        playerRepository.deleteAll();
        gameRepository.deleteAll();
        game = new Game();
        game.setGameId( UUID.randomUUID() );
        game.setGameStatus(RUNNING);
        game.setCurrentRoundCount( 22 );
        gameRepository.save( game );
    }


    @Test
    public void noExceptionWhenConnectionMissing() {
        playerApplicationService.createPlayer();
        assert( true );
    }

    @Test
    public void testCreatePlayers() {
        // given
        playerApplicationService.createPlayer();

        // when
        List<Player> allPlayers = playerRepository.findAll();

        // then
        assertEquals( 1, allPlayers.size() );
        for ( Player player: allPlayers ) {
            assertNotNull( player.getEmail(), "player email" );
            assertNotNull( player.getName(), "player name"  );
            assertFalse( player.isReadyToPlay(), "should not be ready to play" );
        }
    }


    @Test
    public void testRegisterPlayers() throws Exception {
        // given
        playerApplicationService.createPlayer();
        List<Player> allPlayers = playerRepository.findAll();
        for ( Player player: allPlayers ) {
            mockRegistrationEndpointFor( player, game.getGameId() );
            mockBearerTokenEndpointFor( player );
        }

        // when
        playerApplicationService.obtainBearerTokenForPlayer();

        // then
        allPlayers = playerRepository.findAll();
        assertEquals( 1, allPlayers.size() );
        for ( Player player: allPlayers ) {
            assertNotNull( player.getEmail(), "player email" );
            assertNotNull( player.getName(), "player name"  );
            assertNotNull( player.getBearerToken() );
        }
    }


    @Test
    public void testDoublyRegisterPlayers() throws Exception {
        // given
        playerApplicationService.createPlayer();
        List<Player> allPlayers = playerRepository.findAll();
        for ( Player player: allPlayers ) {
            mockRegistrationEndpointFor( player, game.getGameId() );
            mockBearerTokenEndpointFor( player );
        }

        // when
        playerApplicationService.obtainBearerTokenForPlayer();
        playerApplicationService.obtainBearerTokenForPlayer();

        // then
        allPlayers = playerRepository.findAll();
        assertEquals( 1, allPlayers.size() );
        for ( Player player: allPlayers ) {
            assertNotNull( player.getEmail(), "player email" );
            assertNotNull( player.getName(), "player name"  );
            assertNotNull( player.getBearerToken() );
        }
    }

}
