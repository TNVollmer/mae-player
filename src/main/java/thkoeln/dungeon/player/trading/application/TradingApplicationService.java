package thkoeln.dungeon.player.trading.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.events.concreteevents.trading.TradablePricesEvent;

@Service
public class TradingApplicationService {

    private static final Logger log = LoggerFactory.getLogger(TradingApplicationService.class);

    @Async
    @EventListener(TradablePricesEvent.class)
    public void updateTradePrices(TradablePricesEvent event) {
        //TODO: save prices
        log.info("{}", event.getTradeableItems());
    }
}
