package thkoeln.dungeon.eventconsumer.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.player.application.PlayerApplicationService;

import java.util.UUID;

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

    public void thisIsJustAnUnusedTestMethod() {
        // this method does nothing, just used as a test case for merging
    }

    /**
     * "Status changed" event published by GameService, esp. after a game has been created, started, or finished
     */
    @KafkaListener( topics = "status" )  // that is what the documentation says
    public void consumeGameStatusEvent( @Header String eventId, @Header String timestamp, @Header String transactionId,
                                        @Payload String payload ) {
        GameStatusEvent gameStatusEvent = new GameStatusEvent( eventId, timestamp, transactionId, payload );
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

/*
    //@KafkaListener( topics = "status" )  // that is what the documentation says
    public void consumeGameStatusEvent2( ProducerRecord<String, String> consumerRecord ) {
        try {
            GameStatusEventPayloadDto gameStatusEventPayload = GameStatusEventPayloadDto.fromJsonString( consumerRecord.value() );
            GameStatusEvent gameStatusEvent = new GameStatusEvent(null, gameStatusEventPayload);
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
            logger.error( "Error converting payload for Game Status Changed event: " + consumerRecord.value() );
        }
    }

*/

    public void consumeNewRoundStartedEvent() {
        // todo
    }
}
