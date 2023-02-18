package thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotRevealedDto {
    private UUID robotId;
    private UUID planetId;
    private String playerNotion;
    private Integer health;
    private Integer energy;
    private RobotRevealedLevelDto levels;

    // this is set by the PlayerEventHandler class. It describes the 1-char code for enemy players,
    // display purposes in the client.
    private Character enemyChar = null;

    public static final Integer DEFAULT_STRENGTH = 10;

    /**
     * Factory method for testing purposes
     * @param robotId
     * @param planetId
     * @param playerShortName
     * @param enemyChar
     * @return
     */
    public static RobotRevealedDto defaultsFor( UUID robotId, UUID planetId, String playerShortName, Character enemyChar ) {
        RobotRevealedDto robotRevealedDto = new RobotRevealedDto();
        robotRevealedDto.setRobotId( robotId );
        robotRevealedDto.setPlanetId( planetId );
        robotRevealedDto.setEnemyChar( enemyChar );
        robotRevealedDto.setPlayerNotion( playerShortName );
        robotRevealedDto.setHealth( DEFAULT_STRENGTH );
        robotRevealedDto.setEnergy( DEFAULT_STRENGTH );
        robotRevealedDto.setLevels( RobotRevealedLevelDto.defaults() );
        return robotRevealedDto;
    }


    public boolean isValid() {
        return ( robotId != null && planetId != null  && playerNotion != null );
    }
}
