package thkoeln.dungeon.monte.trading.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.monte.core.eventlistener.concreteevents.trading.TradeablePricesEvent;
import thkoeln.dungeon.monte.trading.domain.TradingAccount;
import thkoeln.dungeon.monte.trading.domain.TradingAccountRepository;
import thkoeln.dungeon.monte.trading.domain.TradingException;

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

    public void updateCreditBalance( Money newCreditBalance ) {
        if ( newCreditBalance == null ) throw new TradingException( "newCreditBalance == null" );
        TradingAccount tradingAccount = queryAndIfNeededCreateTradingAccount();
        tradingAccount.setCreditBalance( newCreditBalance );
        tradingAccountRepository.save( tradingAccount );
    }

    public void save( TradingAccount tradingAccount ) {
        if ( tradingAccount == null ) throw new TradingException( "tradingAccount == null" );
        tradingAccountRepository.save( tradingAccount );
    }
}
