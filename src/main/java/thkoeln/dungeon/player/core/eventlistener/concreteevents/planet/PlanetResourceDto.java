package thkoeln.dungeon.player.core.eventlistener.concreteevents.planet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetResourceDto {
    private MineableResourceType resourceType;
    private Integer maxAmount;
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
