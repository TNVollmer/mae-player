package thkoeln.dungeon.player.core.events.concreteevents.robot.mine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;

import static thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType.*;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotResourceInventoryDto {
    @JsonProperty( "COAL" )
    private int coal = 0;
    @JsonProperty( "IRON" )
    private int iron = 0;
    @JsonProperty( "GEM" )
    private int gem = 0;
    @JsonProperty( "GOLD" )
    private int gold = 0;
    @JsonProperty( "PLATIN" )
    private int platin = 0;


    public MineableResource getResource() {
        if ( coal > 0 ) return MineableResource.fromTypeAndAmount( COAL, coal );
        if ( iron > 0 ) return MineableResource.fromTypeAndAmount( IRON, iron );
        if ( gem > 0 ) return MineableResource.fromTypeAndAmount( GEM, gem );
        if ( gold > 0 ) return MineableResource.fromTypeAndAmount( GOLD, gold );
        if ( platin > 0 ) return MineableResource.fromTypeAndAmount( PLATIN, platin );
        return null;
    }


    public boolean isValid() {
        return ( coal >= 0 && iron >= 0 && gem >= 0 && gold >= 0 && platin >= 0 );
    }
}
