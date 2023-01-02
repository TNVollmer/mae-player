package thkoeln.dungeon.monte.core.strategy;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.trading.domain.TradingAccount;

import static thkoeln.dungeon.monte.core.domainprimitives.command.CommandType.MOVEMENT;
import static thkoeln.dungeon.monte.core.domainprimitives.command.CommandType.BUYING;
import static thkoeln.dungeon.monte.core.domainprimitives.command.CommandType.REGENERATE;
import static thkoeln.dungeon.monte.core.strategy.ExampleBehavior.WhatToDo.*;

@Getter
@Setter
public class ExampleBehavior implements Behavior {
    protected enum WhatToDo { move, buySomething, getWellSoon };
    private WhatToDo todoFlag;

    public Command move() {
        if ( todoFlag == move ) return Command.ofType( MOVEMENT );
        return null;
    }

    public Command buySomething( AccountInformation accountInformation ) {
        if ( todoFlag == buySomething ) return Command.ofType( BUYING );
        return null;
    }

    public Command getWellSoon() {
        if ( todoFlag == getWellSoon ) return Command.ofType( REGENERATE );
        return null;
    }
}
