package thkoeln.dungeon.player.core.events.concreteevents.planet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceMinedEvent extends AbstractEvent {
    @JsonProperty("planet")
    private UUID planetId;

    private Integer minedAmount;

    private PlanetResourceDto resource;

    @Override
    public boolean isValid() {
        if (planetId == null) return false;
        return minedAmount > 0;
    }


}
