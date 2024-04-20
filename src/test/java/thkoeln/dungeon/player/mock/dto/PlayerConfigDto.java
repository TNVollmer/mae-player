package thkoeln.dungeon.player.mock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerConfigDto {

    private UUID id;

    @JsonProperty("name")
    private String playerName;
    @JsonProperty("email")
    private String mailAddress;

    private Double balance;

}
