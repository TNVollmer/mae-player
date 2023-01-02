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
    private ExampleBehavior behavior;
    TradingAccount tradingAccount;

    @BeforeEach
    public void setUp() {
        strategy = new ExampleStrategy();
        behavior = new ExampleBehavior();
        tradingAccount = new TradingAccount();
    }


    @Test
    public void testMove() {
        // given
        behavior.setTodoFlag( ExampleBehavior.WhatToDo.move );

        // when
        Command command = strategy.decideNextCommand( behavior, tradingAccount );

        // then
        assertEquals( CommandType.MOVEMENT, command.getCommandType() );
    }


    @Test
    public void testBuy() {
        // given
        behavior.setTodoFlag( ExampleBehavior.WhatToDo.buySomething );

        // when
        Command command = strategy.decideNextCommand( behavior, tradingAccount );

        // then
        assertEquals( CommandType.BUYING, command.getCommandType() );
    }


    @Test
    public void testNoCommand() {
        // given
        behavior.setTodoFlag( ExampleBehavior.WhatToDo.getWellSoon );

        // when
        Command command = strategy.decideNextCommand( behavior, tradingAccount );

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
            strategy.decideNextCommand( behavior, tradingAccount );
        });
    }

}
