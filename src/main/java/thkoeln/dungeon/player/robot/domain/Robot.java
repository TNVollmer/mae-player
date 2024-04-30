package thkoeln.dungeon.player.robot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.player.core.domainprimitives.robot.Inventory;
import thkoeln.dungeon.player.core.domainprimitives.status.Activity;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.player.domain.Player;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Robot {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID robotId;

    @ManyToOne
    private Player player;

    @ManyToOne
    private Planet planet;

    //TODO: add inventory and stats
    @Embedded
    private Inventory inventory;

    @ElementCollection
    private List<Capability> stats;

    private boolean isAlive = true;
    private Activity currentActivity = Activity.IDLE;


    public Robot(UUID robotId, Player player, Planet planet) {
        this.robotId = robotId;
        this.player = player;
        this.planet = planet;
    }

    public void move(CompassDirection direction) {
        planet = planet.getNeighbor(direction);
    }

    public boolean canMine() {
        return planet.hasResources();
    }

}
