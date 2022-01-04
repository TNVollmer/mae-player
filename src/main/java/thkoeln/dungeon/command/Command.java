package thkoeln.dungeon.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import thkoeln.dungeon.robot.domain.Robot;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

/**
 * Should be abstract, but that conflicts with JPS
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Command {
    @Id
    private final UUID id = UUID.randomUUID();
    private CommandType commandType;
    @ManyToOne
    private Robot robot;
}
