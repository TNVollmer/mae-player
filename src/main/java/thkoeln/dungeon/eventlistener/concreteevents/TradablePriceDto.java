package thkoeln.dungeon.eventlistener.concreteevents;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TradablePriceDto {
    private String name;
    private Integer price;
    private String type;
}
