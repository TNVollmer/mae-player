package thkoeln.dungeon.player.core.domainprimitives.planet;

import jakarta.persistence.Embeddable;
import lombok.*;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class Planet {
    private UUID planetId;
    private UUID gameWorldId;
    private int movementDifficulty;
    private MineableResource resourceType;

    public Planet(UUID planetId, UUID gameWorldId, int movementDifficulty, String resourceType) {
        this.planetId = planetId;
        this.gameWorldId = gameWorldId;
        this.movementDifficulty = movementDifficulty;

        // allowed resource types: "COAL", "IRON", "GEM", "GOLD", "PLATIN" -> Platinum?
        try {
            MineableResourceType.valueOf(resourceType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid resource type: " + resourceType);
        }
    }
}
