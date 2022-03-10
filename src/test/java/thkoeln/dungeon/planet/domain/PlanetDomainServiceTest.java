package thkoeln.dungeon.planet.domain;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.core.EventPayloadTestFactory;
import thkoeln.dungeon.domainprimitives.CompassDirection;
import thkoeln.dungeon.domainprimitives.Coordinate;
import thkoeln.dungeon.eventconsumer.core.AbstractEventTest;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.player.domain.Player;

import java.util.*;

import static org.apache.commons.lang3.math.NumberUtils.min;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.dungeon.game.domain.GameStatus.CREATED;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlanetDomainServiceTest extends AbstractEventTest {
    private final int maxX = 5;
    private final int maxY = 3;
    private UUID[][] planetIds = new UUID[maxX][maxY];

    // ---------------------
    // |   |xHx| S |   |   |
    // ---------------------
    // |   |   |   |   |xHx|
    // ---------------------
    // | S |   |   |   | S |
    // ---------------------
    // holes at 1,2 and at 4,1
    private Integer[][] holes = new Integer[][] {{0, 0, 0}, {0, 0, 1}, {0, 0, 0}, {0, 0, 0}, {0, 1, 0}};
    // space stations at 0,0 / 2,2 / 4,0
    private Integer[][] spacestations = new Integer[][] {{1, 0, 0}, {0, 0, 0}, {0, 0, 1}, {0, 0, 0}, {1, 0, 0}};

    @Autowired
    private PlanetRepository planetRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        planetRepository.deleteAll();
        setUpTestPlanetData();
    }


    private void setUpTestPlanetData() {
        super.spaceStationIds.clear();
        for ( int x = 0; x < maxX; x++ ) {
            for ( int y = 0; y < maxY; y++ ) {
                if ( holes[x][y] == 0 ) {
                    planetIds[x][y] = UUID.randomUUID();
                }
                else {
                    planetIds[x][y] = null;
                }
                if ( spacestations[x][y] > 0 ) {
                    super.spaceStationIds.add( planetIds[x][y] );
                }
            }
        }
    }

    private Map<CompassDirection, UUID> findNeighbourIds( int x, int y ) {
        Map<CompassDirection, UUID> neighbourIds = new HashMap<>();
        for ( CompassDirection compassDirection : CompassDirection.values() ) {
            int newX = x + compassDirection.xOffset();
            int newY = y + compassDirection.yOffset();
            if ( newX < 0 || newX >= maxX ) continue;
            if ( newY < 0 || newY >= maxY ) continue;
            UUID neighbourId = planetIds[newX][newY];
            if ( neighbourId != null ) {
                neighbourIds.put( compassDirection, neighbourId );
            }
        }
        return neighbourIds;
    }


    private void visit( int x, int y ) throws Exception {
        UUID movementTransactionId = UUID.randomUUID();
        UUID visitedId = planetIds[x][y];
        robotEventConsumerService.consumeMovementEvent(
                UUID.randomUUID().toString(), EventPayloadTestFactory.timestamp(), movementTransactionId.toString(),
                EventPayloadTestFactory.movementPayload( planetIds[x][y] ) );
        robotEventConsumerService.consumeNeighboursEvent(
                UUID.randomUUID().toString(), EventPayloadTestFactory.timestamp(), movementTransactionId.toString(),
                EventPayloadTestFactory.neighboursPayload( findNeighbourIds( x, y ) ) );
        gameEventConsumerService.consumeRoundStatusEvent(
                UUID.randomUUID().toString(), EventPayloadTestFactory.timestamp(), genericTransactionIdStr,
                EventPayloadTestFactory.roundStatusPayload( gameId, "started" ) );
    }



    @Test
    public void testNumberOfPlanets() throws Exception {
        // given
        super.setUpGame();
        super.setUpPlayer();
        super.startGame( super.spaceStationIds );
        List<Planet> spacestations = planetRepository.findBySpacestationEquals( true );
        assertEquals( 3, spacestations.size() );

        // when
        visit( 0, 0 );
        visit( 2, 2 );
        visit( 4, 0 );

        // then
        // 3 space stations and 5 regular planets, as neighbours
        spacestations = planetRepository.findBySpacestationEquals( true );
        assertEquals( 3, spacestations.size() );
        List<Planet> allPlanets = planetRepository.findAll();
        assertEquals( 8, allPlanets.size() );
    }


    @Test
    public void testOneStepSouthFrom22() throws Exception {
        // given
        // all space stations
        super.setUpGame();
        super.setUpPlayer();
        super.startGame( super.spaceStationIds );
        List<Planet> spacestations = planetRepository.findBySpacestationEquals( true );
        assertEquals( 3, spacestations.size() );
        visit( 0, 0 );
        visit( 2, 2 );
        visit( 4, 0 );

        // when
        visit( 2, 1 );

        // then
        // you should have a bridge from 2,1 to 2,2
        Planet planet = planetRepository.findByPlanetId( planetIds[2][1] ).orElseThrow( () -> new RuntimeException() );
        planet = planet.getNorthNeighbour();
        assertEquals( planetIds[2][2], planet.getPlanetId() );

        planet = planetRepository.findByPlanetId( planetIds[2][1] ).orElseThrow( () -> new RuntimeException() );
        assertTrue( planet.hasBeenVisited() );
        assertTrue( planet.getNorthNeighbour().hasBeenVisited() );
        assertFalse( planet.getEastNeighbour().hasBeenVisited() );
        assertFalse( planet.getSouthNeighbour().hasBeenVisited() );
        assertFalse( planet.getWestNeighbour().hasBeenVisited() );
    }


    @Test
    public void testTwoStepsSouthFrom22() throws Exception {
        // given
        // all space stations
        super.setUpGame();
        super.setUpPlayer();
        super.startGame( super.spaceStationIds );
        List<Planet> spacestations = planetRepository.findBySpacestationEquals( true );
        assertEquals( 3, spacestations.size() );
        visit( 0, 0 );
        visit( 2, 2 );
        visit( 4, 0 );

        // when
        visit( 2, 1 );
        visit( 2, 0 );

        // then
        // you should have a bridge from 0,0 to 2,2
        Planet planet = planetRepository.findByPlanetId( planetIds[0][0] ).orElseThrow( () -> new RuntimeException() );
        planet = planet.getEastNeighbour().getEastNeighbour().getNorthNeighbour().getNorthNeighbour();
        assertEquals( planetIds[2][2], planet.getPlanetId() );

        // ... and a bridge from 0,0 to 0,4
        planet = planetRepository.findByPlanetId( planetIds[0][0] ).orElseThrow( () -> new RuntimeException() );
        planet = planet.getEastNeighbour().getEastNeighbour().getEastNeighbour().getEastNeighbour();
        assertEquals( planetIds[4][0], planet.getPlanetId() );

        // ... and a bridge from 0,4 to 2,2
        planet = planetRepository.findByPlanetId( planetIds[4][0] ).orElseThrow( () -> new RuntimeException() );
        planet = planet.getWestNeighbour().getWestNeighbour().getNorthNeighbour().getNorthNeighbour();
        assertEquals( planetIds[2][2], planet.getPlanetId() );
    }


    @Test
    public void testTwoStepsSouthFrom22_alsoNonTrivialConnectionSet() throws Exception {
        // given
        // all space stations
        super.setUpGame();
        super.setUpPlayer();
        super.startGame( super.spaceStationIds );
        List<Planet> spacestations = planetRepository.findBySpacestationEquals( true );
        assertEquals( 3, spacestations.size() );
        visit( 0, 0 );
        visit( 2, 2 );
        visit( 4, 0 );

        // when
        visit( 2, 1 );
        visit( 2, 0 );

        // then
        // the following pathes have not been walked directly, but should also be available
        Planet planet = planetRepository.findByPlanetId( planetIds[0][0] ).orElseThrow( () -> new RuntimeException() );
        planet = planet.getNorthNeighbour().getEastNeighbour().getSouthNeighbour();
        assertEquals( planetIds[1][0], planet.getPlanetId() );

        planet = planetRepository.findByPlanetId( planetIds[2][0] ).orElseThrow( () -> new RuntimeException() );
        planet = planet.getEastNeighbour().getNorthNeighbour().getWestNeighbour().getSouthNeighbour();
        assertEquals( planetIds[2][0], planet.getPlanetId() );
    }

}
