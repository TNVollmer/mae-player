package thkoeln.dungeon.player.robot.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class RobotPlanet {
    private UUID planetId;
    private UUID north;
    private UUID east;
    private UUID south;
    private UUID west;

    @Setter
    private int movementDifficulty;

    @Embedded
    private MineableResource mineableResource;

    public UUID randomNonNullNeighbourId() {
        UUID[] neighbours = {north, east, south, west};
        List<UUID> nonNullNeighbours = Arrays.stream(neighbours).filter(Objects::nonNull).toList();
        if (nonNullNeighbours.isEmpty()) {
            return null;
        }
        return nonNullNeighbours.get((int) (Math.random() * nonNullNeighbours.size()));
    }

    public static RobotPlanet nullPlanet() {
        return new RobotPlanet(null, null, null, null, null, 0, null);
    }

    public static RobotPlanet planetWithoutNeighbours(UUID planetId) {
        return new RobotPlanet(planetId, null, null, null, null, 0, null);
    }

    public static RobotPlanet planetWithNeighbours(UUID planetId, PlanetNeighboursDto[] neighbours, int movementDifficulty, MineableResource resourceType) {
        UUID north = null;
        UUID east = null;
        UUID south = null;
        UUID west = null;
        for (PlanetNeighboursDto neighbour : neighbours) {
            switch (neighbour.getDirection()) {
                case NORTH:
                    north = neighbour.getId();
                    break;
                case EAST:
                    east = neighbour.getId();
                    break;
                case SOUTH:
                    south = neighbour.getId();
                    break;
                case WEST:
                    west = neighbour.getId();
                    break;
            }
        }
        return new RobotPlanet(planetId, north, east, south, west, movementDifficulty, resourceType);
    }

    public void updateMineableResource(MineableResource resource) {
        try {
            if (this.mineableResource.getType().equals(resource.getType())) {
                this.mineableResource.subtractAmount(resource.getAmount());
                log.info("RESSOURCE --> " + "Updated mineable resource: " + this.mineableResource.getType() + " with amount: " + this.mineableResource.getAmount());
            }else {
                log.info("RESSOURCE --> " + "Updated mineable resource: " + this.mineableResource.getType() + " with amount: " + this.mineableResource.getAmount());
            }

        } catch (Exception e) {
            this.mineableResource = null;
            log.error("RESSOURCE --> " + "No mineable resource on planet: " + this.planetId);
        }
    }


    public UUID[] getNeighbours() {
        return new UUID[]{north, east, south, west};
    }
}
