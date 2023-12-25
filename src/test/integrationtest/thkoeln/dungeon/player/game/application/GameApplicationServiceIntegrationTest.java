package thkoeln.dungeon.player.game.application;


import org.junit.jupiter.api.Assertions;
import thkoeln.dungeon.player.core.AbstractDungeonMockingIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import thkoeln.dungeon.player.DungeonPlayerConfiguration;
import thkoeln.dungeon.player.game.domain.Game;
import thkoeln.dungeon.player.game.domain.GameRepository;
import thkoeln.dungeon.player.game.domain.GameStatus;


@DirtiesContext
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class GameApplicationServiceIntegrationTest extends AbstractDungeonMockingIntegrationTest {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameApplicationService gameApplicationService;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        gameRepository.deleteAll();
    }


    @Test
    public void testRunningGameAvailable() throws Exception {
        // given
        mockGamesGetWithRunning();

        // when
        gameApplicationService.fetchRemoteGame();

        // then
        Game game = gameApplicationService.queryActiveGame();
        Assertions.assertNotNull( game );
        Assertions.assertEquals( GameStatus.STARTED, game.getGameStatus() );
    }

    @Test
    public void testCreatedGameAvailable() throws Exception {
        // given
        mockGamesGetWithCreated();

        // when
        gameApplicationService.fetchRemoteGame();

        // then
        Game game = gameApplicationService.queryActiveGame();
        Assertions.assertNotNull( game );
        Assertions.assertEquals( GameStatus.CREATED, game.getGameStatus() );
    }
}
