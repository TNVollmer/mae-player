package thkoeln.dungeon.monte.restadapter;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class PlayerJoinDto {
    private String gameExchange;
    private String playerQueue;
}
