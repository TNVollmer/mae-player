package thkoeln.dungeon.eventconsumer.robot;

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
public class MovementEvent extends AbstractEvent {
    private Boolean success;
    private String message;
    private Integer remainingEnergy;
    @Embedded
    @JsonProperty( "planet" )
    private MovedToPlanetDto movedToPlanetDto;

    @ElementCollection
    @JsonProperty( "robots" )
    @CollectionTable(name = "robot_ids_in_movement_event", joinColumns = @JoinColumn(name = "id_for_uuid"))
    @Column(name = "robot_ids_in_movement_event")
    private final List<UUID> robotIds = new ArrayList<>();

    public boolean isValid() {
        return ( success != null );
    }

    @Override
    public String toString() {
        return message;
    }
}
