package thkoeln.dungeon.monte.core.strategy;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.domainprimitives.purchasing.ItemType;
import thkoeln.dungeon.monte.trading.domain.TradingAccount;

import java.util.UUID;

import static thkoeln.dungeon.monte.core.domainprimitives.command.CommandType.MOVEMENT;
import static thkoeln.dungeon.monte.core.domainprimitives.command.CommandType.BUYING;
import static thkoeln.dungeon.monte.core.domainprimitives.command.CommandType.REGENERATE;
import static thkoeln.dungeon.monte.core.strategy.ExampleBehavior.WhatToDo.*;

@Getter
@Setter
public class ExampleBehavior implements Behavior {
    protected enum WhatToDo { move, buySomething, getWellSoon };
    private WhatToDo todoFlag;
    private final static UUID DUMMY_ID = UUID.randomUUID();

    public Command move() {
        if ( todoFlag == move ) return Command.createMove( DUMMY_ID, DUMMY_ID, DUMMY_ID, DUMMY_ID );
        return null;
    }

    public Command buySomething( AccountInformation accountInformation ) {
        if ( todoFlag == buySomething )
            return Command.createItemPurchase( ItemType.ENERGY_RESTORE, 1, DUMMY_ID, DUMMY_ID, DUMMY_ID );
        return null;
    }

    public Command getWellSoon() {
        if ( todoFlag == getWellSoon ) return Command.createRegeneration( DUMMY_ID, DUMMY_ID, DUMMY_ID );
        return null;
    }
}
