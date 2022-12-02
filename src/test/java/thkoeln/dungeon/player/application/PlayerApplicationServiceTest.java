package thkoeln.dungeon.player.application;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.core.AbstractDungeonMockingTest;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlayerApplicationServiceTest extends AbstractDungeonMockingTest {


    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerApplicationService playerApplicationService;
    private Game game;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        playerRepository.deleteAll();
        gameRepository.deleteAll();
    }


    @Test
    public void testCreatePlayer_noPlayer() throws Exception {
        // given
        mockPlayerGetNotFound();
        mockPlayerPost();

        // when
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();

        // then
        assertEquals( playerEmail, player.getEmail(), "player email" );
        assertEquals( playerName, player.getName(), "player name" );
        assertTrue( player.isReadyToPlay(), "should be ready to play" );
    }



    @Test
    public void testCreatePlayer_thereIsAPlayer() throws Exception {
        // given
        mockPlayerGetFound();
        mockPlayerPost();

        // when
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();

        // then
        assertEquals( playerEmail, player.getEmail(), "player email" );
        assertEquals( playerName, player.getName(), "player name" );
        assertTrue( player.isReadyToPlay(), "should be ready to play" );
    }




    @Test
    public void testFetchPlayer() throws Exception {
        // given
        // when
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        assertNotNull( player.getEmail(), "player email" );
        assertNotNull( player.getName(), "player name"  );
        assertNotNull( player.getPlayerId() );
    }


    @Test
    public void testDoublyRegisterPlayers() throws Exception {
        // given
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        mockRegistrationEndpointFor( player, game.getGameId() );
        mockPlayerPost();

        // when
        playerApplicationService.registerPlayer();
        playerApplicationService.registerPlayer();

        // then
        player = playerApplicationService.queryAndIfNeededCreatePlayer();
        assertNotNull( player.getEmail(), "player email" );
        assertNotNull( player.getName(), "player name"  );
        assertNotNull( player.getPlayerId() );
    }


}
