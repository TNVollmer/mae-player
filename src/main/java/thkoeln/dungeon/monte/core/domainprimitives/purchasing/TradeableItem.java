package thkoeln.dungeon.monte.core.domainprimitives.purchasing;

import lombok.*;
import javax.persistence.Embeddable;

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
