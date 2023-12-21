package thkoeln.dungeon.player.robot.domain;


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
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Robot {
    @Transient
    private Logger logger = LoggerFactory.getLogger(Robot.class);

    @Id
    UUID robotId;

    private UUID planetId;
    private String playerNotion;
    private Health health;
    private Energy energy;
    //TODO: Hier fehlt noch eine Representation der Level

}
