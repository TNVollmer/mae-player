package thkoeln.dungeon.player.application;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.core.AbstractDungeonMockingTest;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.restadapter.PlayerRegistryDto;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static thkoeln.dungeon.game.domain.GameStatus.*;

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
        playerApplicationService.createPlayer();

        // then
        Player player = playerApplicationService.fetchPlayer().orElseThrow(
            () -> new RuntimeException( "No player!" ));
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
        playerApplicationService.createPlayer();

        // then
        Player player = playerApplicationService.fetchPlayer().orElseThrow(
                () -> new RuntimeException( "No player!" ));
        assertEquals( playerEmail, player.getEmail(), "player email" );
        assertEquals( playerName, player.getName(), "player name" );
        assertTrue( player.isReadyToPlay(), "should be ready to play" );
    }



    @Test
    public void testRegisterPlayer() throws Exception {
        // given
        playerApplicationService.createPlayer();
        List<Player> allPlayers = playerRepository.findAll();
        for ( Player player: allPlayers ) {
            mockRegistrationEndpointFor( player, game.getGameId() );
            mockPlayerPost();
        }

        // then
        allPlayers = playerRepository.findAll();
        assertEquals( 1, allPlayers.size() );
        for ( Player player: allPlayers ) {
            assertNotNull( player.getEmail(), "player email" );
            assertNotNull( player.getName(), "player name"  );
            assertNotNull( player.getPlayerId() );
        }
    }


    @Test
    public void testDoublyRegisterPlayers() throws Exception {
        // given
        playerApplicationService.createPlayer();
        List<Player> allPlayers = playerRepository.findAll();
        for ( Player player: allPlayers ) {
            mockRegistrationEndpointFor( player, game.getGameId() );
            mockPlayerPost();
        }

        // when
        playerApplicationService.registerPlayer();
        playerApplicationService.registerPlayer();

        // then
        allPlayers = playerRepository.findAll();
        assertEquals( 1, allPlayers.size() );
        for ( Player player: allPlayers ) {
            assertNotNull( player.getEmail(), "player email" );
            assertNotNull( player.getName(), "player name"  );
            assertNotNull( player.getPlayerId() );
        }
    }


}
