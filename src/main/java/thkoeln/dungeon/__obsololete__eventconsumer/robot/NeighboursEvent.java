package thkoeln.dungeon.__obsololete__eventconsumer.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.__obsololete__eventconsumer.core.AbstractEvent;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor ( access = AccessLevel.PROTECTED )
@JsonIgnoreProperties( ignoreUnknown = true )
public class NeighboursEvent extends AbstractEvent {
    @ElementCollection ( fetch = FetchType.EAGER )
    @JsonProperty( "neighbours" )
    private final List<NeighbourPlanetDto> neighbourPlanetDtos = new ArrayList<>();

    public boolean isValid() {
        return true;
    }

    @Override
    public String toString() {
        String retVal = "";
        for ( NeighbourPlanetDto neighbourPlanetDto: neighbourPlanetDtos ) {
            retVal += String.valueOf( neighbourPlanetDto.toString() );
            retVal += " || ";
        }
        return retVal;
    }
}
