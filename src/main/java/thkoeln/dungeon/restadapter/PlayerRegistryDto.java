package thkoeln.dungeon.restadapter;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class PlayerRegistryDto {
    private String name;
    private String email;
    private UUID bearerToken;

    public PlayerRegistryDto clone() {
        PlayerRegistryDto myClone = new PlayerRegistryDto();
        myClone.setBearerToken( this.bearerToken );
        myClone.setName( this.name );
        myClone.setEmail( this.email );
        return myClone;
    }
}
