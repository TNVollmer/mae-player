package thkoeln.dungeon.player.planet.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;

import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Planet {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID planetId;

    private Integer movementDifficulty;

    @Embedded
    private MineableResource resources;

    @OneToOne
    private Planet northNeighbor;
    @OneToOne
    private Planet eastNeighbor;
    @OneToOne
    private Planet southNeighbor;
    @OneToOne
    private Planet westNeighbor;

    public Planet(UUID planetId, MineableResource resources) {
        this.planetId = planetId;
        this.resources = resources;
    }

    public Planet getNeighbor(CompassDirection direction) {
        return switch (direction) {
            case NORTH -> northNeighbor;
            case EAST -> eastNeighbor;
            case SOUTH -> southNeighbor;
            case WEST -> westNeighbor;
        };
    }

    public boolean hasResources() {
        return !resources.isEmpty();
    }
}
