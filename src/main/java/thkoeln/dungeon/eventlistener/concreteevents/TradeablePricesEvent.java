package thkoeln.dungeon.eventlistener.concreteevents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.domainprimitives.Money;
import thkoeln.dungeon.domainprimitives.TradeableItem;
import thkoeln.dungeon.domainprimitives.TradeableType;
import thkoeln.dungeon.eventlistener.AbstractEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TradeablePricesEvent extends AbstractEvent {
    private List<TradeableItem> tradeableItems = new ArrayList<>();

    public boolean isValid() {
        return ( tradeableItems.size() > 0 );
    }

    /**
     * As the body consists of an array, we need special treatment here ...
     * @param jsonString
     */
    @Override
    public void fillWithPayload( String jsonString ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            TradeableItemDto[] tradeableItemDtos = objectMapper.readValue( jsonString, TradeableItemDto[].class );
            for ( TradeableItemDto tradableItemDto : tradeableItemDtos ) {
                TradeableItem tradeableItem =  new TradeableItem(
                        tradableItemDto.getName(),
                        Money.fromInteger( tradableItemDto.getPrice() ),
                        TradeableType.valueOf( tradableItemDto.getType() ) );
                tradeableItems.add(tradeableItem);
            }
        }
        catch( JsonProcessingException conversionFailed ) {
            logger.error( "Error converting payload for TradeablePricesEvent with jsonString " + jsonString );
        }
    }

    @Override
    public String toString() {
        // todo temp
        if ( 1 == 1 ) return toStringDetailed();
        // todo temp
        String retVal = "TradeablePricesEvent: " + eventHeader;
        if ( tradeableItems.size() == 0 ) {
            retVal += "\n\tNo tradablePriceDtos!";
        }
        else {
            retVal += "\n\t" + tradeableItems.get( 0 ) +
                    " (plus " + String.valueOf( tradeableItems.size()-1 ) + " more)";
        }
        return retVal;
    }

    public String toStringDetailed() {
        String retVal = "TradeablePricesEvent: " + eventHeader;
        for ( TradeableItem tradeableItem : tradeableItems) {
            retVal += tradeableItem + "\n";
        }
        return retVal;
    }
}
