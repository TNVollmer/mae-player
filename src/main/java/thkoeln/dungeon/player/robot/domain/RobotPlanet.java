package thkoeln.dungeon.player.robot.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor (access = AccessLevel.PROTECTED)
public class RobotPlanet {
    private UUID planetId;
    private UUID north;
    private UUID east;
    private UUID south;
    private UUID west;

    public UUID randomNonNullNeighbourId(){
        UUID[] neighbours = {north, east, south, west};
        List<UUID> nonNullNeighbours = Arrays.stream(neighbours).filter(Objects::nonNull).collect(Collectors.toList());
        if(nonNullNeighbours.isEmpty()){
            return null;
        }
        return nonNullNeighbours.get((int) (Math.random() * nonNullNeighbours.size()));
    }
    public static RobotPlanet nullPlanet(){
        return new RobotPlanet(null, null, null, null, null);
    }

    public static RobotPlanet planetWithoutNeighbours(UUID planetId){
        return new RobotPlanet(planetId, null, null, null, null);
    }

    public static RobotPlanet planetWithNeighbours(UUID planetId, PlanetNeighboursDto[] neighbours){
        UUID north = null;
        UUID east = null;
        UUID south = null;
        UUID west = null;
        for(PlanetNeighboursDto neighbour : neighbours){
            switch (neighbour.getDirection()){
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
        return new RobotPlanet(planetId, north, east, south, west);
    }


}
