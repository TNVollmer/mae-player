package integrationtest.thkoeln.dungeon.player.planet.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import thkoeln.dungeon.player.DungeonPlayerConfiguration;
import thkoeln.dungeon.player.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.player.core.domainprimitives.location.MineableResource;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetNeighboursDto;
import thkoeln.dungeon.player.core.events.concreteevents.planet.PlanetResourceDto;
import thkoeln.dungeon.player.core.events.concreteevents.planet.ResourceMinedEvent;
import thkoeln.dungeon.player.planet.application.PlanetApplicationService;
import thkoeln.dungeon.player.planet.domain.Planet;
import thkoeln.dungeon.player.planet.domain.PlanetRepository;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static thkoeln.dungeon.player.core.domainprimitives.location.MineableResourceType.COAL;

@DirtiesContext
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
@ActiveProfiles( "test" )
public class PlanetEventTest {

    protected Planet p1;

    @Autowired
    protected PlanetRepository planetRepository;
    @Autowired
    protected RobotRepository robotRepository;

    protected PlanetApplicationService planetApplicationService;

    @BeforeEach
    public void setUp() {
        planetApplicationService = new PlanetApplicationService(planetRepository, robotRepository);

        p1 = new Planet(UUID.randomUUID());
        p1.setResources(MineableResource.fromTypeAndAmount(COAL, 10));
        p1.setMovementDifficulty(1);

        planetRepository.save(p1);
    }

    @Test
    public void testPlanetDiscoveredEvent() {
        PlanetDiscoveredEvent event = new PlanetDiscoveredEvent();
        UUID id = UUID.randomUUID();
        event.setPlanetId(id);
        event.setMovementDifficulty(1);

        PlanetNeighboursDto neighboursDto = new PlanetNeighboursDto();
        neighboursDto.setDirection(CompassDirection.NORTH);
        UUID neighbourId = UUID.randomUUID();
        neighboursDto.setId(neighbourId);
        List<PlanetNeighboursDto> neighbours = new ArrayList<>();
        neighbours.add(neighboursDto);
        event.setNeighbours(neighbours.toArray(new PlanetNeighboursDto[0]));

        PlanetResourceDto resourceDto = new PlanetResourceDto();
        resourceDto.setResourceType(COAL);
        resourceDto.setCurrentAmount(10);
        resourceDto.setMaxAmount(20);
        event.setResource(resourceDto);

        planetApplicationService.onPlanetDiscovered(event);

        Planet planet = planetRepository.findByPlanetId(id).orElseThrow();

        assertEquals(id, planet.getPlanetId());
        assertEquals(COAL, planet.getResources().getType());
        assertEquals(10, planet.getResources().getAmount());
    }

    @Test
    public void testResourceRemovedEvent() {
        ResourceMinedEvent event = new ResourceMinedEvent();
        event.setPlanetId(p1.getPlanetId());
        PlanetResourceDto dto = new PlanetResourceDto();

        dto.setCurrentAmount(8);
        dto.setResourceType(COAL);
        dto.setMaxAmount(20);
        event.setResource(dto);
        event.setMinedAmount(2);

        planetApplicationService.onPlanetResourceMined(event);

        Planet planet = planetRepository.findByPlanetId(p1.getPlanetId()).orElseThrow();

        assertEquals(8, planet.getResources().getAmount());
    }
}
