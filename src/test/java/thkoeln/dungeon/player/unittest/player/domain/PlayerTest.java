package thkoeln.dungeon.player.unittest.player.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
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
        player.setPlayerId( UUID.randomUUID() );
        assertTrue( player.isRegistered() );

        // when / then
        player.setPlayerExchange( "someString" );
        assertTrue( player.isRegistered() );
    }
}
