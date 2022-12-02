package thkoeln.dungeon.player.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlayerRegistrationWithoutConnectionTest {
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    PlayerApplicationService playerApplicationService;
    @Value("${dungeon.playerName}")
    protected String playerName;
    @Value("${dungeon.playerEmail}")
    protected String playerEmail;

    @BeforeEach
    public void setUp() {
        playerRepository.deleteAll();
    }


    @Test
    public void testCreatePlayerWithNoConnection() {
        // given
        // when
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();

        // then
        assertEquals( playerEmail, player.getEmail(), "player email" );
        assertEquals( playerName, player.getName(), "player name" );
        assertFalse( player.isRegistered(), "should not be registered" );
    }
}
