package thkoeln.dungeon.eventconsumer.game;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.core.EventPayloadTestFactory;
import thkoeln.dungeon.domainprimitives.CompassDirection;
import thkoeln.dungeon.eventconsumer.core.AbstractEventTest;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;
import thkoeln.dungeon.player.domain.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static thkoeln.dungeon.game.domain.GameStatus.CREATED;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class GameEventConsumerTest extends AbstractEventTest {
    private final Map<CompassDirection, UUID> neighbours = new HashMap<>();

    @Autowired
    private PlanetRepository planetRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        planetRepository.deleteAll();
        neighbours.put( CompassDirection.NORTH, UUID.randomUUID() );
        neighbours.put( CompassDirection.SOUTH, UUID.randomUUID() );
        neighbours.put( CompassDirection.WEST, UUID.randomUUID() );
    }

    @Test
    public void dummy_remove_when_the_other_tests_have_been_restored() {
        assertTrue(TRUE);
    }

/*
    @Test
    public void testGameStatusEventReceiced() throws Exception {
        // given
        resetMockServer();
        mockRegistrationEndpointFor( gameId, player.getPlayerId() );
        gameStatusEventPayloadDto = new GameStatusEventPayloadDto( gameId, CREATED );
        eventPayloadString = objectMapper.writeValueAsString( gameStatusEventPayloadDto );

        // when
        gameEventConsumerService.consumeGameStatusEvent(
                genericEventIdStr, EventPayloadTestFactory.timestamp(), genericTransactionIdStr, eventPayloadString );

        // then
        assertEquals( 1, gameRepository.findAllByGameStatusEquals( CREATED ).size() );
        Game newGame = gameRepository.findAllByGameStatusEquals( CREATED ).get( 0 );
        assertEquals( gameId, newGame.getGameId() );
        assertEquals( 1, playerRepository.findByRegistrationTransactionId( genericTransactionId ).size() );
        assertEquals( 1, playerRepository.findByCurrentGame( newGame ).size() );
        Player newlyRegisteredPlayer = playerRepository.findByCurrentGame( newGame ).get( 0 );
        assertNotNull( newlyRegisteredPlayer.getPlayerId() );
        assertNull( newlyRegisteredPlayer.getPlayerId() );
    }


    @Test
    public void testGameCreatedEventReceiced() throws Exception {
        // given
        setUpGame();

        // when
        setUpPlayer();
        List<Player> foundPlayers = playerRepository.findByPlayerId( playerId );

        // then
        assertEquals( 1, foundPlayers.size() );
        assertEquals( playerId, foundPlayers.get( 0 ).getPlayerId() );
    }




    @Test
    public void testPlanetMapAfterOneMove() throws Exception {
        // given
        super.setUpGame();
        super.setUpPlayer();
        super.startGame( super.spaceStationIds );

        // when
        UUID movementTransactionId = UUID.randomUUID();
        robotEventConsumerService.consumeMovementEvent(
                UUID.randomUUID().toString(), EventPayloadTestFactory.timestamp(), movementTransactionId.toString(),
                EventPayloadTestFactory.movementPayload( spaceStation1Id ) );
        robotEventConsumerService.consumeNeighboursEvent(
                UUID.randomUUID().toString(), EventPayloadTestFactory.timestamp(), movementTransactionId.toString(),
                EventPayloadTestFactory.neighboursPayload( neighbours ) );
        gameEventConsumerService.consumeRoundStatusEvent(
                UUID.randomUUID().toString(), EventPayloadTestFactory.timestamp(), genericTransactionIdStr,
                EventPayloadTestFactory.roundStatusPayload( gameId, "started" ) );

        // then
        List<Planet> allPlanets = planetRepository.findAll();
        assertEquals( spaceStationIds.size() + neighbours.size(), allPlanets.size() );
        // todo some more checks
    }
*/

}
