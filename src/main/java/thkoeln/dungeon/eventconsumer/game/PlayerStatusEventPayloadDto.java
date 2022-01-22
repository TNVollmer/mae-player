package thkoeln.dungeon.eventconsumer.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerStatusEventPayloadDto {
    @JsonProperty("userId")
    private UUID playerId;

    public static PlayerStatusEventPayloadDto fromJsonString( String jsonString ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        return objectMapper.readValue( jsonString, PlayerStatusEventPayloadDto.class );
    }

    public PlayerStatusEventPayloadDto( String userIdString ) {
        setPlayerId( UUID.fromString( userIdString ) );
    }
}
