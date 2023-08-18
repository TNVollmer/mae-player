package thkoeln.dungeon.monte.player.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotRevealedDto;
import thkoeln.dungeon.monte.robot.domain.Robot;

import static java.lang.Boolean.FALSE;

/**
 * This class is a helper that maps the DTOs used in events to the domain robot entity.
 */
@Service
public class RobotDtoMapper {
    private Logger logger = LoggerFactory.getLogger( RobotDtoMapper.class );

    public void updateRobot( Robot robot, RobotRevealedDto robotRevealedDto, boolean warnIfDifference ) {
        if ( robotRevealedDto.getEnergy() != null &&
                !robotRevealedDto.getEnergy().equals( robotRevealedDto.getEnergy() ) ) {
            robot.setEnergy(Energy.from( robotRevealedDto.getEnergy() ) );
            if ( warnIfDifference ) logger.warn( "Robot " + robot + " has different energy level than expected. " +
                    " (expected: " + robot.getEnergy() + ", received via event: " + robotRevealedDto.getEnergy() + ")" ) ;
        }


        /* ---- TODO
        private Integer health;
        private Integer healthLevel;
        private Integer damageLevel;
        private Integer miningSpeedLevel;
        private Integer miningLevel;
        private Integer energyLevel;
        private Integer energyRegenLevel;
        private Integer storageLevel;
         */
    }
}
