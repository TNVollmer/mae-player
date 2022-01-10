package thkoeln.dungeon.player.application;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.core.AbstractRESTEndpointMockingTest;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlayerGameRegistrationTest extends AbstractRESTEndpointMockingTest {
    private Player player, playerWithoutToken;
    @Autowired
    private Environment env;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerApplicationService playerApplicationService;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        playerRepository.deleteAll();
        gameRepository.deleteAll();
        game = Game.newlyCreatedGame( UUID.randomUUID() );
        gameRepository.save( game );
        player = new Player();
        playerWithoutToken = new Player();
        playerRepository.save(player);
        playerRepository.save(playerWithoutToken);
    }


    @Test
    public void testRegisterPlayerWithToken() throws Exception {
        // given
        mockBearerTokenEndpointFor( player );
        playerApplicationService.obtainBearerTokenForPlayer( player );
        assert ( player.isReadyToPlay() );
        super.resetMockServer();
        mockBearerTokenEndpointFor( player );
        mockRegistrationEndpointFor( player, game.getGameId() );

        // when
        playerApplicationService.registerOnePlayerForGame( player, game );

        // then
        List<Player> readyPlayers = playerRepository.findByGameParticipations_Game( game );
        assertEquals( 1, readyPlayers.size() );
        assert( readyPlayers.get( 0 ).isParticipantInGame( game ) );
    }


}
