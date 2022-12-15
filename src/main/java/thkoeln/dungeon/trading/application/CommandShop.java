package thkoeln.dungeon.trading.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.trading.domain.TradingAccount;

import javax.annotation.PostConstruct;

/**
 * This is a factory where commands can be "purchased" - depending on the amount of money available.
 */

@Service
public class CommandShop {
    private Logger logger = LoggerFactory.getLogger( CommandShop.class );
    private TradingAccount tradingAccount;
    private TradingAccountApplicationService tradingAccountApplicationService;

    @Autowired
    public CommandShop( TradingAccountApplicationService tradingAccountApplicationService ) {
        this.tradingAccountApplicationService = tradingAccountApplicationService;
    }

    @PostConstruct
    public void initialize() throws Exception {
        tradingAccount =  tradingAccountApplicationService.queryAndIfNeededCreateTradingAccount();
    }

}
