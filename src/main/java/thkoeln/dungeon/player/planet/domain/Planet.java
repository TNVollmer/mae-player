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
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

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

    public List<Planet> getNeighbors() {
        List<Planet> neighbors = new ArrayList<>();
        for (CompassDirection direction : CompassDirection.values()) {
            Planet neighbor = getNeighbor(direction);
            if (neighbor != null)
                neighbors.add(neighbor);
        }
        return neighbors;
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
            if (neighbor != null && !neighbor.isExplored()) neighbors.add(neighbor);
        }
        return neighbors;
    }

    public List<Planet> getPathToNearestPlanetWithResources() {
        return searchInMap(Planet::hasResources);
    }

    public List<Planet> getPathToNearestPlanetWithResource(MineableResourceType resourceType) {
        return searchInMap((Planet planet) -> planet.hasResources() && planet.getResources().getType() == resourceType);
    }

    public List<Planet> getPathToNearestUnexploredPlanet() {
        return searchInMap((Planet planet)  -> !planet.isExplored);
    }

    public List<Planet> getPathToPlanet(Planet endPoint) {
        return searchInMap((Planet planet)  -> planet == endPoint);
    }

    private List<Planet> searchInMap(Predicate<Planet> condition) {
        HashMap<Planet, List<Planet>> graph = new HashMap<>();
        graph.put(this, new ArrayList<>());
        List<Planet> visited = new ArrayList<>();
        visited.add(this);

        while (!graph.isEmpty()) {
            Planet current = graph.keySet().iterator().next();
            graph.remove(current);

            for (Planet planet : current.getNeighbors()) {
                if (visited.contains(planet)) continue;
                visited.add(planet);

                List<Planet> directions = new ArrayList<>(graph.get(current));
                directions.add(current);
                graph.put(planet, directions);

                if (condition.test(planet)) return graph.get(planet);
            }
        }
        return null;
    }

    public void explore() {
        this.isExplored = true;
    }

    public boolean isExplored() {
        return isExplored;
    }

    public boolean hasResource(MineableResourceType resourceType) {
        return hasResources() && resources.getType() == resourceType;
    }

    public boolean hasResources() {
        return resources != null && !resources.isEmpty();
    }

    public void minedResource(MineableResource resource) {
        this.resources = this.resources.subtract(resource);
    }
}
