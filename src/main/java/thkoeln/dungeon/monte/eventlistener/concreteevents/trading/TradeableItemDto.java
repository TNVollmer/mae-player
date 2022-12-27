package thkoeln.dungeon.monte.eventlistener.concreteevents.trading;

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
