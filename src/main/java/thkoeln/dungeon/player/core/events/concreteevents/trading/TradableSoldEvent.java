package thkoeln.dungeon.player.core.events.concreteevents.trading;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TradableSoldEvent extends AbstractEvent {

    private UUID playerId;
    private UUID robotId;
    private String type;
    private String name;
    private Integer amount;
    private Integer pricePerUnit;
    private Integer totalPrice;

    @Override
    public boolean isValid() {
        if (eventHeader == null) return false;
        return (playerId != null && type != null && name != null && amount != null && pricePerUnit != null && totalPrice != null);
    }

}
