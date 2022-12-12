package thkoeln.dungeon.domainprimitives;

import lombok.*;
import javax.persistence.Embeddable;

@NoArgsConstructor( access = AccessLevel.PROTECTED )
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
@ToString
public class TradableItem {
    private String name;
    private Moneten price;
    private TradableType type;
}
