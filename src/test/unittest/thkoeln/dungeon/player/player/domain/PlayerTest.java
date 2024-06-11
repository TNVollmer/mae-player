package thkoeln.dungeon.player.player.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    protected String playerName;
    protected String playerEmail;

    @BeforeEach
    public void setUp() {
        playerName = "WhatAName";
        playerEmail = "What@aname.de";
    }

    @Test
    public void testStatus() {
        // given
        Player player = Player.ownPlayer( playerName, playerEmail );
        assertFalse( player.isRegistered() );

        // when / then
        player.setPlayerId( UUID.randomUUID() );
        assertTrue( player.isRegistered() );

        // when / then
        player.setPlayerExchange( "someString" );
        assertTrue( player.isRegistered() );
    }

    @Test
    public void budgetTest() {
        Player player = Player.ownPlayer(playerName, playerEmail);

        Assertions.assertEquals(0, player.getBankAccount().getAmount());

        Money money = Money.from(100);
        Money percentage = money.getPercentage(20);
        Money stacking = money.increaseBy(money).increaseBy(money);

        player.depositInBank(money);

        Assertions.assertEquals(20, percentage.getAmount());
        Assertions.assertEquals(300, stacking.getAmount());
        Assertions.assertEquals(money, player.getBankAccount());
        Assertions.assertEquals(20, player.getMiscBudget().getAmount());
        Assertions.assertEquals(40, player.getUpgradeBudget().getAmount());
        Assertions.assertEquals(40, player.getNewRobotsBudget().getAmount());
    }
}
