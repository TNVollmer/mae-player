package thkoeln.dungeon.__obsololete__eventconsumer.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.__obsololete__eventconsumer.core.AbstractEvent;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor ( access = AccessLevel.PROTECTED )
public class GameWorldCreatedEvent extends AbstractEvent {
    private UUID id;
    private String status;

    @ElementCollection
    @JsonProperty( "spacestation_ids" )
    @CollectionTable(name = "space_station_ids_in_Game_World_Created_Event", joinColumns = @JoinColumn(name = "id_for_uuid"))
    @Column(name = "space_station_ids_in_Game_World_Created_Event")
    private final List<UUID> spaceStationIds = new ArrayList<>();

    public boolean isValid() {
        return ( !spaceStationIds.isEmpty() );
    }

    @Override
    public String toString() {
        return String.valueOf( spaceStationIds );
    }
}
