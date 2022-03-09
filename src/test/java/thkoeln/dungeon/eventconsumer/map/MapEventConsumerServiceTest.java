package thkoeln.dungeon.eventconsumer.map;



import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.core.AbstractDungeonMockingTest;
import thkoeln.dungeon.core.EventPayloadTestFactory;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class MapEventConsumerServiceTest extends AbstractDungeonMockingTest {
    private List<UUID> planetIds;
    private UUID pid1, pid2, pid3;

    @Autowired
    MapEventConsumerService mapEventConsumerService;
    @Autowired
    PlanetRepository planetRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        planetRepository.deleteAll();
        pid1 = UUID.randomUUID();
        pid2 = UUID.randomUUID();
        pid3 = UUID.randomUUID();
        planetIds = new ArrayList<>();
        planetIds.add( pid1 );
        planetIds.add( pid2 );
        planetIds.add( pid3 );
    }

    @Test
    public void testConsumeGameworldCreated() throws Exception {
        // given
        // when
        mapEventConsumerService.consumeGameWorldCreatedEvent(
                genericEventIdStr, EventPayloadTestFactory.timestamp(), genericTransactionIdStr,
                EventPayloadTestFactory.gameworldCreatedPayload( planetIds ) );

        // then
        List<Planet> allPlanets = planetRepository.findAll();
        assertEquals( 3, allPlanets.size() );
        Optional<Planet> found = planetRepository.findByPlanetId( pid2 );
        assertTrue( found.isPresent() );
        assertTrue( found.get().isSpaceStation() );
    }

    @Test
    public void testConsumeSpacestationCreated() throws Exception {
        // given
        // when
        mapEventConsumerService.consumeSpaceStationCreatedEvent(
                genericEventIdStr, EventPayloadTestFactory.timestamp(), genericTransactionIdStr,
                EventPayloadTestFactory.spaceStationCreatedPayload( pid1 ) );

        // then
        List<Planet> allPlanets = planetRepository.findAll();
        assertEquals( 1, allPlanets.size() );
        Optional<Planet> found = planetRepository.findByPlanetId( pid1 );
        assertTrue( found.isPresent() );
        assertTrue( found.get().isSpaceStation() );
    }

}
