package thkoeln.dungeon.player.robot.domain;


import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.player.core.domainprimitives.status.Energy;
import thkoeln.dungeon.player.core.domainprimitives.status.Health;

import java.util.UUID;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Robot {
    @Transient
    private Logger logger = LoggerFactory.getLogger(Robot.class);

    @Id
   private final UUID id = UUID.randomUUID();

    private UUID robotId;

    @Embedded
    @Setter
    private RobotPlanet robotPlanet = RobotPlanet.nullPlanet();
   public Robot(UUID robotId, UUID planetId){
       if(robotId == null || planetId == null){
           logger.error("Robot or planet id is null");
           throw new IllegalArgumentException("Robot or planet id is null");
       }
       this.robotId = robotId;
         this.robotPlanet = RobotPlanet.planetWithoutNeighbours(planetId);
   }

   public static Robot of(UUID robotId, UUID planetId){
       return new Robot(robotId, planetId);
   }
}
