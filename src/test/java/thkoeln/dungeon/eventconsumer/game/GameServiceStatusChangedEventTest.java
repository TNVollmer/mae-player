package thkoeln.dungeon.eventconsumer.game;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.core.AbstractRESTEndpointMockingTest;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.player.domain.PlayerRepository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.messaging.MessageHeaders.ID;
import static org.springframework.messaging.MessageHeaders.TIMESTAMP;
import static thkoeln.dungeon.eventconsumer.core.AbstractEvent.TRANSACTION_ID_KEY;
import static thkoeln.dungeon.game.domain.GameStatus.*;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class GameServiceStatusChangedEventTest extends AbstractRESTEndpointMockingTest {
    @Autowired
    private PlayerApplicationService playerApplicationService;
    @Autowired
    private GameApplicationService gameApplicationService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameEventConsumerService gameEventConsumerService;

    private List<Player> players;
    private UUID bearerToken = UUID.randomUUID();
    private UUID gameId = UUID.randomUUID();
    private UUID transactionId = UUID.randomUUID();
    private GameStatusEventPayloadDto createdEventPayload;
    private String eventPayloadString;
    private MessageHeaders messageHeaders;
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        playerRepository.deleteAll();
        gameRepository.deleteAll();
        playerApplicationService.createPlayers();

        players = playerRepository.findAll();
        for ( Player player: players ) mockBearerTokenEndpointFor( player );
        for ( Player player: players ) {
            playerApplicationService.obtainBearerTokenForPlayer( player );
            assertTrue( player.isReadyToPlay() );
            playerRepository.save( player );
        }
        createdEventPayload = new GameStatusEventPayloadDto( gameId, CREATED );
        HashMap<String, Object> map = new HashMap<>();
        map.put( ID, UUID.randomUUID() );
        map.put( TIMESTAMP, 999999L );
        map.put( TRANSACTION_ID_KEY, transactionId );
        messageHeaders = new MessageHeaders( map );
    }

    @Test
    public void testGameCreatedEventReceiced() throws Exception {
        // given
        resetMockServer();
        for ( Player player: players ) mockRegistrationEndpointFor( player, gameId );
        eventPayloadString = objectMapper.writeValueAsString( createdEventPayload );

        // when
        gameEventConsumerService.consumeGameStatusEvent( eventPayloadString, messageHeaders );

        // then
        assertEquals( 1, gameRepository.findAll().size() );
        assertEquals( gameId, gameRepository.findAllByGameStatusEquals( CREATED ).get( 0 ).getGameId() );
        for ( Player player: players ) {
            player.setBearerToken( bearerToken );
            assertTrue( player.isReadyToPlay() );
        }
    }
}
