package thkoeln.dungeon.eventconsumer.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import thkoeln.dungeon.game.domain.GameStatus;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameStatusEventPayloadDto {
    @JsonProperty("gameId")
    private UUID gameId;
    @JsonProperty("status")
    private GameStatus gameStatus;

    public static GameStatusEventPayloadDto fromJsonString( String jsonString ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        return objectMapper.readValue( jsonString, GameStatusEventPayloadDto.class );
    }

    public GameStatusEventPayloadDto( String gameIdString, String gameStatusString ) {
        setGameStatus( GameStatus.valueOf( gameIdString ) );
        setGameId( UUID.fromString( gameIdString ) );
    }
}
