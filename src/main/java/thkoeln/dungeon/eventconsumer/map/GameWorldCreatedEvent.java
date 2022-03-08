package thkoeln.dungeon.eventconsumer.map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.eventconsumer.core.AbstractEvent;
import thkoeln.dungeon.game.domain.GameStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor ( access = AccessLevel.PROTECTED )
public class GameWorldCreatedEvent extends AbstractEvent {
    @ElementCollection
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
