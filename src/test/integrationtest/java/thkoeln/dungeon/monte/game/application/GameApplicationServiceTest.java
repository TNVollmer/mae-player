package thkoeln.dungeon.monte.game.application;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.monte.DungeonPlayerConfiguration;
import thkoeln.dungeon.monte.core.AbstractDungeonMockingTest;
import thkoeln.dungeon.monte.game.domain.Game;
import thkoeln.dungeon.monte.game.domain.GameRepository;
import thkoeln.dungeon.monte.game.domain.GameStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class GameApplicationServiceTest extends AbstractDungeonMockingTest {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameApplicationService gameApplicationService;

    @Before
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
        assertNotNull( game );
        assertEquals( GameStatus.RUNNING, game.getGameStatus() );
    }

    @Test
    public void testCreatedGameAvailable() throws Exception {
        // given
        mockGamesGetWithCreated();

        // when
        gameApplicationService.fetchRemoteGame();

        // then
        Game game = gameApplicationService.queryActiveGame();
        assertNotNull( game );
        assertEquals( GameStatus.CREATED, game.getGameStatus() );
    }
}
