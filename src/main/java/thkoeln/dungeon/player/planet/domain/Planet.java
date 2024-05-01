package thkoeln.dungeon.player.planet.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;

import java.util.ArrayList;
import java.util.List;
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

    private boolean isExplored = false;

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

    public Planet(UUID planetId) {
        this.planetId = planetId;
    }

    public Planet(UUID planetId, MineableResource resources) {
        this.planetId = planetId;
        this.resources = resources;
    }

    public void addNeighbor(Planet neighbor, CompassDirection direction) {
        switch (direction) {
            case NORTH -> northNeighbor = neighbor;
            case EAST ->  eastNeighbor = neighbor;
            case SOUTH -> southNeighbor = neighbor;
            case WEST ->  westNeighbor = neighbor;
        }
    }

    public Planet getNeighbor(CompassDirection direction) {
        return switch (direction) {
            case NORTH -> northNeighbor;
            case EAST -> eastNeighbor;
            case SOUTH -> southNeighbor;
            case WEST -> westNeighbor;
        };
    }

    public List<Planet> getNeighborWithResources() {
        List<Planet> neighbors = new ArrayList<>();
        for (CompassDirection direction : CompassDirection.values()) {
            Planet neighbor = getNeighbor(direction);
            if (neighbor.hasResources()) neighbors.add(neighbor);
        }
        return neighbors;
    }

    public List<Planet> getUnexploredNeighbors() {
        List<Planet> neighbors = new ArrayList<>();
        for (CompassDirection direction : CompassDirection.values()) {
            Planet neighbor = getNeighbor(direction);
            if (!neighbor.isExplored()) neighbors.add(neighbor);
        }
        return neighbors;
    }

    public void explore() {
        this.isExplored = true;
    }

    public boolean isExplored() {
        return isExplored;
    }

    public boolean hasResources() {
        return resources != null && !resources.isEmpty();
    }

    public void minedResource(MineableResource resource) {
        this.resources = this.resources.subtract(resource);
    }
}
