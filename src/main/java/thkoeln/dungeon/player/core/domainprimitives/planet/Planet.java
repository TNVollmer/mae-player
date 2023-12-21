package thkoeln.dungeon.player.core.domainprimitives.planet;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@Getter
@Setter( AccessLevel.PROTECTED )
@EqualsAndHashCode
@Embeddable
public class Planet {
    private UUID planetId;
    private UUID gameWorldId;
    private int movementDifficulty;
    private String resourceType;

    public Planet(UUID planetId, UUID gameWorldId, int movementDifficulty, String resourceType) {
        this.planetId = planetId;
        this.gameWorldId = gameWorldId;
        this.movementDifficulty = movementDifficulty;

        // allowed resource types: "COAL", "IRON", "GEM", "GOLD", "PLATIN" -> Platinum?
        if (Objects.equals(resourceType, "COAL") || Objects.equals(resourceType, "IRON") || Objects.equals(resourceType, "GEM") || Objects.equals(resourceType, "GOLD") || Objects.equals(resourceType, "PLATIN")) {
            this.resourceType = resourceType;
        } else {
            throw new IllegalArgumentException("Invalid resource type!");
        }
    }
}
