package thkoeln.dungeon.player.player.application;


import thkoeln.dungeon.player.core.AbstractDungeonMockingIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import thkoeln.dungeon.player.DungeonPlayerConfiguration;
import thkoeln.dungeon.player.player.domain.PlayerRepository;

@DirtiesContext
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
class PlayerEnemyIntegrationTest extends AbstractDungeonMockingIntegrationTest {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerApplicationService playerApplicationService;


    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        playerRepository.deleteAll();
    }
}
