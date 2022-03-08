package thkoeln.dungeon.planet.application;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.domainprimitives.Coordinate;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlanetApplicationServiceTest {
    private UUID pid1, pid2, pid3;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    PlanetApplicationService planetApplicationService;

    @BeforeEach
    public void setUp() {
        planetRepository.deleteAll();
        pid1 = UUID.randomUUID();
        pid2 = UUID.randomUUID();
        pid3 = UUID.randomUUID();
    }

    @Test
    public void testIfInitialPlanetHasInitialCoordinates_forSpacestation() {
        // given
        // when
        planetApplicationService.addPlanetWithoutNeighbours( pid1, true );
        Optional<Planet> found = planetRepository.findByPlanetId( pid1 );

        // then
        assertTrue( found.isPresent() );
        Planet planet = found.get();
        assertTrue( planet.isSpaceStation() );
        assertEquals( Coordinate.initialCoordinate(), planet.getCoordinate() );
    }

    @Test
    public void testIfInitialPlanetHasInitialCoordinates_forRegularPlanet() {
        // given
        // when
        planetApplicationService.addPlanetWithoutNeighbours( pid1, false );
        Optional<Planet> found = planetRepository.findByPlanetId( pid1 );

        // then
        assertTrue( found.isPresent() );
        Planet planet = found.get();
        assertFalse( planet.isSpaceStation() );
        assertEquals( Coordinate.initialCoordinate(), planet.getCoordinate() );
    }


    @Test
    public void testAddSeveralPlanets() {
        // given
        // when
        planetApplicationService.addPlanetWithoutNeighbours( pid1, true );
        planetApplicationService.addPlanetWithoutNeighbours( pid2, false );
        planetApplicationService.addPlanetWithoutNeighbours( pid3, true );

        // then
        List<Planet> allPlanets = planetRepository.findAll();
        assertEquals( 3, allPlanets.size() );
        Optional<Planet> found1 = planetRepository.findByCoordinate_XAndCoordinate_Y( 0, 0 );
        assertTrue( found1.isPresent() );
        assertEquals( Coordinate.initialCoordinate(), found1.get().getCoordinate() );
        Optional<Planet> found2 = planetRepository.findByPlanetId( pid2 );
        assertTrue( found2.isPresent() );
        assertNull( found2.get().getCoordinate() );
        Optional<Planet> found3 = planetRepository.findByPlanetId( pid3 );
        assertTrue( found3.isPresent() );
        assertNull( found3.get().getCoordinate() );
    }
}
