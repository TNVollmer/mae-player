package thkoeln.dungeon.monte.player.application;


import thkoeln.dungeon.monte.core.AbstractDungeonMockingIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import thkoeln.dungeon.monte.DungeonPlayerConfiguration;
import thkoeln.dungeon.monte.player.domain.Player;
import thkoeln.dungeon.monte.player.domain.PlayerRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
