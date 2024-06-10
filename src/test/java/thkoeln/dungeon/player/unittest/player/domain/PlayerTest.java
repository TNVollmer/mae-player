package thkoeln.dungeon.player.player.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.player.domain.Player;

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
        player.assignPlayerId( UUID.randomUUID() );
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

        Assertions.assertEquals(20, percentage.getAmount());

        player.depositInBank(money);

        Assertions.assertEquals(money, player.getBankAccount());
        Assertions.assertEquals(20, player.getMiscBudget().getAmount());
        Assertions.assertEquals(40, player.getUpgradeBudget().getAmount());
        Assertions.assertEquals(40, player.getNewRobotsBudget().getAmount());
    }
}
