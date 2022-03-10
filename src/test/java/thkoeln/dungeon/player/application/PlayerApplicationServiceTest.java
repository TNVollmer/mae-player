package thkoeln.dungeon.player.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlayerApplicationServiceTest {
    private final ArrayList<UUID> validPlayerIds = new ArrayList<>();
    private final ArrayList<UUID> invalidPlayerIds = new ArrayList<>();

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    PlayerApplicationService playerApplicationService;

    @BeforeEach
    public void setUp() {
        playerRepository.deleteAll();

        // Invalid PlayerIds
        for (int i = 0; i < 5; i++)
            invalidPlayerIds.add(UUID.randomUUID());

        // Valid PlayerIds
        for (int i = 0; i < 5; i++) {
            Player newPlayer = new Player();
            newPlayer.setPlayerId(UUID.randomUUID());
            playerRepository.save(newPlayer);
            validPlayerIds.add(newPlayer.getPlayerId());
        }
    }

    @Test
    public void testAdjustBankAccount_ForValidPlayer() {
        // given
        UUID validPlayerId = validPlayerIds.get( 0 );
        int newMonetenValue = 9001;

        // when
        playerApplicationService.adjustBankAccount(validPlayerId, newMonetenValue);

        // then
        Player updatedPlayer = playerApplicationService.findUniquePlayerById(validPlayerId);
        assertEquals( newMonetenValue, updatedPlayer.getMoneten().getAmount() );
    }

    @Test
    public void testAdjustBankAccount_ForInvalidPlayer() {
        // given
        UUID validPlayerId = invalidPlayerIds.get( 0 );

        // when
        // then
        assertThrows(
                PlayerApplicationException.class,
                () -> playerApplicationService.adjustBankAccount(validPlayerId, 420)
        );
    }
}
