package thkoeln.dungeon.monte.player.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal.RobotRevealedDto;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.robot.application.RobotApplicationService;
import thkoeln.dungeon.monte.robot.domain.Robot;

import static java.lang.Boolean.FALSE;

/**
 * This class is a helper that maps the DTOs used in events to the domain robot entity.
 */
@Service
public class RobotDtoMapper {
    private Logger logger = LoggerFactory.getLogger( RobotDtoMapper.class );
    private PlanetApplicationService planetApplicationService;

    @Autowired
    public RobotDtoMapper( PlanetApplicationService planetApplicationService ) {
        this.planetApplicationService = planetApplicationService;
    }

    public void updateRobot( Robot robot, RobotRevealedDto robotRevealedDto, boolean warnIfDifference ) {
        if ( robot.getLocation() == null || robot.getLocation().getPlanetId() != robotRevealedDto.getPlanetId() ) {
            Planet planet = planetApplicationService.addOrUpdatePlanet( robotRevealedDto.getPlanetId(), null, FALSE );
            if ( warnIfDifference ) logger.warn( "Robot " + robot + " is on different planet than expected. " +
                    " (expected: " + robot.getLocation() + ", received via event: " + planet + ")" ) ;
            robot.moveToPlanet( planet );
        }
        if ( robotRevealedDto.getEnergy() != null &&
                robot.getEnergy().getEnergyAmount() != robotRevealedDto.getEnergy() ) {
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
