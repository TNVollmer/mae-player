package thkoeln.dungeon.game.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.game.domain.GameRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class GameApplicationServiceWithoutConnectionTest {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameApplicationService gameApplicationService;

    @BeforeEach
    public void setUp() {
        gameRepository.deleteAll();
    }


    @Test
    public void testCreatePlayerWithNoConnection() {
        // given
        // when
        gameApplicationService.fetchRemoteGame();

        // then
        assertFalse( gameApplicationService.retrieveActiveGame().isPresent() );
    }
}
