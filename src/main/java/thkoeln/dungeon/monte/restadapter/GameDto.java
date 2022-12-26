package thkoeln.dungeon.monte.restadapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import thkoeln.dungeon.monte.game.domain.GameStatus;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDto {
    private UUID gameId;
    private GameStatus gameStatus;
    private Integer currentRoundNumber;
    private Integer roundLengthInMillis;
    private String[] participatingPlayers = new String[0];
}
