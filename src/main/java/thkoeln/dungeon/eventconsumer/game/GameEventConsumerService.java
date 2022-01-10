package thkoeln.dungeon.eventconsumer.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.player.application.PlayerApplicationService;

@Service
public class GameEventConsumerService {
    private Logger logger = LoggerFactory.getLogger( GameEventConsumerService.class );
    private GameApplicationService gameApplicationService;
    private PlayerApplicationService playerApplicationService;
    private GameStatusEventRepository gameStatusEventRepository;
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();


    @Autowired
    public GameEventConsumerService( GameApplicationService gameApplicationService,
                                     GameStatusEventRepository gameStatusEventRepository,
                                     PlayerApplicationService playerApplicationService ) {
        this.gameApplicationService = gameApplicationService;
        this.gameStatusEventRepository = gameStatusEventRepository;
        this.playerApplicationService = playerApplicationService;
    }


    /**
     * "Status changed" event published by GameService, esp. after a game has been created, started, or finished
     */
    @KafkaListener( topics = "status" )  // that is what the documentation says
    public void consumeGameStatusEvent( @Payload String payloadString, MessageHeaders headers ) {
        try {
            GameStatusEventPayloadDto gameStatusEventPayload = GameStatusEventPayloadDto.fromJsonString(payloadString);
            GameStatusEvent gameStatusEvent = new GameStatusEvent(headers, gameStatusEventPayload);
            gameStatusEventRepository.save(gameStatusEvent);
            if (gameStatusEvent.isValid()) {
                switch (gameStatusEvent.getGameStatus()) {
                    case CREATED:
                        playerApplicationService.joinPlayersInNewlyCreatedGame(gameStatusEvent.getGameId());
                        break;
                    case RUNNING:
                        gameApplicationService.gameExternallyStarted(gameStatusEvent.getGameId());
                        break;
                    case FINISHED:
                        gameApplicationService.gameExternallyFinished(gameStatusEvent.getGameId());
                        break;
                }
            }
        }
        catch( JsonProcessingException conversionFailed ) {
            logger.error( "Error converting payload for Game Status Changed event: " + payloadString );
        }
    }


    public void consumeNewRoundStartedEvent() {
        // todo
    }
}
