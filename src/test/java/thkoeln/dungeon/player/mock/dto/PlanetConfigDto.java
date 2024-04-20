package thkoeln.dungeon.player.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlanetConfigDto {
    private UUID id;

    private Integer x;
    private Integer y;

    private Integer movementDifficulty;

}
