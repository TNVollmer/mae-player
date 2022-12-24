package thkoeln.dungeon.trading.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventlistener.concreteevents.TradeablePricesEvent;
import thkoeln.dungeon.trading.domain.TradingAccount;
import thkoeln.dungeon.trading.domain.TradingAccountRepository;
import thkoeln.dungeon.trading.domain.TradingException;

import java.util.List;

@Service
public class TradingAccountApplicationService {
    private Logger logger = LoggerFactory.getLogger( TradingAccountApplicationService.class );
    private TradingAccountRepository tradingAccountRepository;

    @Autowired
    public TradingAccountApplicationService( TradingAccountRepository tradingAccountRepository ) {
        this.tradingAccountRepository = tradingAccountRepository;
    }

    public TradingAccount queryAndIfNeededCreateTradingAccount() {
        List<TradingAccount> tradingAccounts = tradingAccountRepository.findAll();
        TradingAccount tradingAccount;
        if ( tradingAccounts.size() > 1 ) throw new TradingException( "More than one Trading-Account" );
        if ( tradingAccounts.size() == 0 ) {
            tradingAccount = new TradingAccount();
            tradingAccountRepository.save( tradingAccount );
        }
        else {
            tradingAccount = tradingAccounts.get(0);
        }
        return tradingAccount;
    }

    public void updatePrices( TradeablePricesEvent tradeablePricesEvent ) {
        TradingAccount tradingAccount = queryAndIfNeededCreateTradingAccount();

    }
}
