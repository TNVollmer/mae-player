package thkoeln.dungeon.monte.robot.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.domainprimitives.Capability;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Robot {
    @Id
    private final UUID id = UUID.randomUUID();

    // The ID assigned by the Robot service!
    private UUID robotId;

    @Enumerated( EnumType.STRING )
    private RobotType type;

    boolean alive = true;

    @ElementCollection( fetch = FetchType.EAGER )
    @Getter ( AccessLevel.PROTECTED )
    private final List<Capability> capabilities = Capability.allBaseCapabilities();

    public static Robot of( UUID robotId, RobotType type ) {
        if ( robotId == null ) throw new RobotException( "robotId == null" );
        Robot robot = new Robot();
        robot.robotId = robotId;
        robot.type = type;
        return robot;
    }

    public static Robot of( UUID robotId ) {
        return of( robotId, null );
    }


    public void determineNextCommand() {

    }

    public void regenerateIfLowAndNotAttacked() {
    }

    public void fleeIfAttacked() {
    }

    public void mineIfNotMinedLastRound() {
    }

    public void move() {
    }

    public void mine() {
    }

    public void upgrade() {
    }

    public void attack() {
    }

    @Override
    public String toString() {
        String whoAmI = ( type != null ) ? type.toString() : "Robot";
        return whoAmI + " " + String.valueOf( robotId ).substring( 0, 3 );
    }
}
