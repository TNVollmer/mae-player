package thkoeln.dungeon.eventlistener.concreteevents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.domainprimitives.Moneten;
import thkoeln.dungeon.domainprimitives.TradableItem;
import thkoeln.dungeon.domainprimitives.TradableType;
import thkoeln.dungeon.eventlistener.AbstractEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TradablePricesEvent extends AbstractEvent {
    private List<TradableItem> tradableItems = new ArrayList<>();

    public boolean isValid() {
        return ( tradableItems.size() > 0 );
    }

    /**
     * As the body consists of an array, we need special treatment here ...
     * @param jsonString
     */
    @Override
    public void fillWithPayload( String jsonString ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            TradablePriceDto[] tradablePriceDtos = objectMapper.readValue( jsonString, TradablePriceDto[].class );
            for ( TradablePriceDto tradablePriceDto : tradablePriceDtos ) {
                TradableItem tradableItem =  new TradableItem(
                        tradablePriceDto.getName(),
                        Moneten.fromInteger( tradablePriceDto.getPrice() ),
                        TradableType.valueOf( tradablePriceDto.getType() ) );
                tradableItems.add( tradableItem );
            }
        }
        catch( JsonProcessingException conversionFailed ) {
            logger.error( "Error converting payload for TradablePricesEvent with jsonString " + jsonString );
        }
    }

    @Override
    public String toString() {
        String retVal = "TradablePricesEvent: " + eventHeader;
        if ( tradableItems.size() == 0 ) {
            retVal += "\n\tNo tradablePriceDtos!";
        }
        else {
            retVal += "\n\t" + tradableItems.get( 0 ) +
                    " (plus " + String.valueOf( tradableItems.size()-1 ) + " more)";
        }
        return retVal;
    }

    public String toStringDetailed() {
        String retVal = "TradablePricesEvent: " + eventHeader;
        for ( TradableItem tradableItem : tradableItems ) {
            retVal += tradableItem + "\n";
        }
        return retVal;
    }
}
