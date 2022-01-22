package thkoeln.dungeon.command.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

/**
 * This is the DTO that can be used as Request Body for sending a POST /commands to GameService.
 * Hint: Please use CommandFactory to obtain it!
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommandDto {
    @JsonProperty("gameId")
    private UUID gameId;
    @JsonProperty("playerId")
    private UUID playerId;
    @JsonProperty("robotId")
    private UUID robotId;
    @JsonProperty("commandType")
    private CommandType commandType;
}
