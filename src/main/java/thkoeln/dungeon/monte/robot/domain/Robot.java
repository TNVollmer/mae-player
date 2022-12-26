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

    @Embedded
    private RobotType type;

    @ElementCollection( fetch = FetchType.EAGER )
    @Getter ( AccessLevel.PROTECTED )
    private final List<Capability> capabilities = Capability.allBaseCapabilities();

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

}
