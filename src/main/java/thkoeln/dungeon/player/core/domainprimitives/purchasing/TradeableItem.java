package thkoeln.dungeon.player.core.domainprimitives.purchasing;

import lombok.*;
import jakarta.persistence.Embeddable;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
@ToString
public class TradeableItem {
    private String name;
    private Money price;
    private TradeableType type;
}
