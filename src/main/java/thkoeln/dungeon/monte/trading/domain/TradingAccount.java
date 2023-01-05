package thkoeln.dungeon.monte.trading.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.TradeableItem;
import thkoeln.dungeon.monte.core.strategy.AccountInformation;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
public class TradingAccount implements AccountInformation {
    @Id
    @Setter( AccessLevel.PROTECTED )
    private final UUID id = UUID.randomUUID();

    @Embedded
    @Setter
    private Money creditBalance = Money.from( 0 );

    @ElementCollection( fetch = FetchType.EAGER )
    @Getter( AccessLevel.PROTECTED )
    private final List<TradeableItem> tradeableItems = new ArrayList<>();

    @Transient
    private final Map<String, TradeableItem> tradeableItemsMap = new HashMap<>();

    protected void TradingAccount() {
    }

    public void updatePrices( List<TradeableItem> tradeableItems ) {
        // currently not really needed - all hard coded
    }

    @Override
    public int canBuyThatManyRobotsWith( float shareOfCreditBalance  ) {
        if ( shareOfCreditBalance < 0f || shareOfCreditBalance > 1f )
            throw new TradingException( "shareOfCreditBalance < 0f || shareOfCreditBalance > 1f" );
        return ((int) (creditBalance.getAmount() * shareOfCreditBalance)) / 100;
    }


    @Override
    public void payForCommand( Command command ) {
        // todo - currently only robots, therefore still hardcoded, but this is of course temporary
        if ( command.isRobotPurchase() ) {
            Money amountDue = Money.from( command.getCommandObject().getItemQuantity() * 100 );

        }
    }

}
