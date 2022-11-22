package thkoeln.dungeon.game.application;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.core.AbstractDungeonMockingTest;
import thkoeln.dungeon.game.domain.GameRepository;

import static org.junit.Assert.assertTrue;
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
        assertFalse( gameApplicationService.retrieveActiveGame().isPresent() );
    }
}
