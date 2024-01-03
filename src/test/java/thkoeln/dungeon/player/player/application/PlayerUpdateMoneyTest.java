package thkoeln.dungeon.player.player.application;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import thkoeln.dungeon.player.DungeonPlayerConfiguration;
import thkoeln.dungeon.player.core.AbstractDungeonMockingIntegrationTest;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.game.application.GameApplicationService;
import thkoeln.dungeon.player.game.domain.Game;
import thkoeln.dungeon.player.game.domain.GameRepository;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlayerUpdateMoneyTest extends
    AbstractDungeonMockingIntegrationTest {


    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameApplicationService gameApplicationService;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerApplicationService playerApplicationService;

    @MockBean
    private RabbitAdmin rabbitAdmin;

    private Game game;


    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        playerRepository.deleteAll();
        gameRepository.deleteAll();
    }


    @Test
    public void test_updatePlayerMoney() {
        // given
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        Money moneyPlayerInitial = player.getBalance();
        System.out.println( "Initial money: " + moneyPlayerInitial );

        // when
        Money moneyPlayerNew = new Money( 42 );
        playerApplicationService.updateMoney( moneyPlayerNew );
        player = playerRepository.findById(player.getId()).orElseThrow();

        // then
        System.out.println( "New money: " + player.getBalance());
        assertEquals( moneyPlayerNew, player.getBalance(), "Balance should be updated to 42€");
    }


}
