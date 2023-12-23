package thkoeln.dungeon.player.core.restadapter;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class PlayerRegistryDto {
    @Value("${dungeon.playerName}")
    private String name;
    @Value("${dungeon.playerEmail}")
    private String email;
    private UUID playerId;
    private String playerExchange;
    private String playerQueue;

    public PlayerRegistryDto clone() {
        PlayerRegistryDto myClone = new PlayerRegistryDto();
        myClone.setPlayerId( this.playerId );
        myClone.setName( this.name );
        myClone.setEmail( this.email );
        myClone.setPlayerExchange( this.playerExchange );
        myClone.setPlayerQueue( this.playerQueue );
        return myClone;
    }
}
