package thkoeln.dungeon.eventlistener.concreteevents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.eventlistener.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TradablePricesEvent extends AbstractEvent {
    private TradablePriceDto[] tradablePriceDtos = new TradablePriceDto[0];

    public boolean isValid() {
        return ( tradablePriceDtos.length > 0 );
    }

    /**
     * As the body consists of an array, we need special treatment here ...
     * @param jsonString
     */
    @Override
    public void fillWithPayload( String jsonString ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            tradablePriceDtos = objectMapper.readValue( jsonString, TradablePriceDto[].class );
        }
        catch( JsonProcessingException conversionFailed ) {
            logger.error( "Error converting payload for TradablePricesEvent with jsonString " + jsonString );
        }
    }
}
