package thkoeln.dungeon.monte.trading.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.TradeableItem;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
public class TradingAccount {
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
}
