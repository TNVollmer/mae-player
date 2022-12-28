package thkoeln.dungeon.monte.eventlistener.concreteevents.planet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import thkoeln.dungeon.monte.domainprimitives.CompassDirection;
import thkoeln.dungeon.monte.domainprimitives.MineableResourceType;

import java.util.UUID;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetResourceDto {
    @JsonProperty("resource_type")
    private MineableResourceType resourceType;

    @JsonProperty("max_amount")
    private Integer maxAmount;

    @JsonProperty("current_amount")
    private Integer currentAmount;

    boolean isValid() {
        if ( resourceType == null ) return false;
        if ( maxAmount == null ) return false;
        if ( maxAmount < 0 ) return false;
        if ( currentAmount == null ) return false;
        if ( currentAmount < 0 ) return false;
        if ( maxAmount < currentAmount ) return false;
        return true;
    }
}
