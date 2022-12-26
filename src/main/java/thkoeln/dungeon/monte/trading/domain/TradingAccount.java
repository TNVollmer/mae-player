package thkoeln.dungeon.monte.trading.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.monte.domainprimitives.Money;
import thkoeln.dungeon.monte.domainprimitives.TradeableItem;
import thkoeln.dungeon.monte.player.domain.Player;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
public class TradingAccount {
    @Transient
    private Logger logger = LoggerFactory.getLogger( Player.class );

    @Id
    @Setter( AccessLevel.PROTECTED )
    private final UUID id = UUID.randomUUID();

    @Embedded
    @Setter
    private Money money = Money.fromInteger( 0 );

    @ElementCollection( fetch = FetchType.EAGER )
    @Getter( AccessLevel.PROTECTED )
    private final List<TradeableItem> tradeableItems = new ArrayList<>();

    @Transient
    private final Map<String, TradeableItem> tradeableItemsMap = new HashMap<>();

    protected void TradingAccount() {

    }

    public void updatePrices( List<TradeableItem> tradeableItems ) {

    }

}
