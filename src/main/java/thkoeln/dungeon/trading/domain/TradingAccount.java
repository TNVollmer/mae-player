package thkoeln.dungeon.trading.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.domainprimitives.Money;
import thkoeln.dungeon.domainprimitives.TradeableItem;
import thkoeln.dungeon.player.domain.Player;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class TradingAccount {
    @Transient
    private Logger logger = LoggerFactory.getLogger( Player.class );

    @Id
    private final UUID id = UUID.randomUUID();

    @Embedded
    private Money money = Money.fromInteger( 0 );

    @ElementCollection( fetch = FetchType.EAGER )
    private final List<TradeableItem> tradeableItems = new ArrayList<>();
}
