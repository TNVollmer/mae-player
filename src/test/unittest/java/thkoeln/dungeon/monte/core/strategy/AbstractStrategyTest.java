package thkoeln.dungeon.monte.core.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.core.domainprimitives.command.CommandType;
import thkoeln.dungeon.monte.trading.domain.TradingAccount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AbstractStrategyTest {
    private ExampleStrategy strategy;
    private ActionableExample actionable;
    TradingAccount tradingAccount;

    @BeforeEach
    public void setUp() {
        strategy = new ExampleStrategy();
        actionable = new ActionableExample();
        tradingAccount = new TradingAccount();
    }


    @Test
    public void testMove() {
        // given
        actionable.setTodoFlag( ActionableExample.WhatToDo.move );

        // when
        Command command = strategy.findNextCommand( actionable, tradingAccount );

        // then
        assertEquals( CommandType.MOVEMENT, command.getCommandType() );
    }


    @Test
    public void testBuy() {
        // given
        actionable.setTodoFlag( ActionableExample.WhatToDo.buySomething );

        // when
        Command command = strategy.findNextCommand( actionable, tradingAccount );

        // then
        assertEquals( CommandType.BUYING, command.getCommandType() );
    }


    @Test
    public void testNoCommand() {
        // given
        actionable.setTodoFlag( ActionableExample.WhatToDo.getWellSoon );

        // when
        Command command = strategy.findNextCommand( actionable, tradingAccount );

        // then
        assertEquals( CommandType.REGENERATE, command.getCommandType() );
    }


    @Test
    public void testInvalidMethod() {
        // given
        strategy.commandCreatorMethodNames = new String[] { "thisMethodDoesntExist" };

        // then
        // when
        assertThrows( StrategyException.class, () -> {
            strategy.findNextCommand( actionable, tradingAccount );
        });
    }

}
