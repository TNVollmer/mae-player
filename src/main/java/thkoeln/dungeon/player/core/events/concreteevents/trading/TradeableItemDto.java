package thkoeln.dungeon.player.core.events.concreteevents.trading;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TradeableItemDto {
    private String name;
    private Integer price;
    private String type;
}
