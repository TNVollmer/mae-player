package thkoeln.dungeon.eventconsumer.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor ( access = AccessLevel.PROTECTED )
public class SpaceStationCreatedEvent extends AbstractEvent {
    @JsonProperty( "planet_id" )
    private UUID planetId;

    @Override
    public boolean isValid() {
        return ( planetId != null );
    }

    @Override
    public String toString() {
        return String.valueOf( planetId );
    }
}
