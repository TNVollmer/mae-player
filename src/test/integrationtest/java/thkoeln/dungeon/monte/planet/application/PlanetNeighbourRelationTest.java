package thkoeln.dungeon.monte.planet.application;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.monte.DungeonPlayerConfiguration;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.monte.printer.util.MapCoordinate;
import thkoeln.dungeon.monte.printer.util.TwoDimDynamicArray;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.planet.domain.PlanetRepository;
import thkoeln.dungeon.monte.printer.printers.PlanetPrinter;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlanetNeighbourRelationTest {
    private Logger logger = LoggerFactory.getLogger( PlanetNeighbourRelationTest.class );
    private Planet n, s, ne, se, nee, see;
    MapCoordinate c01, c11, c21, c00, c10, c20;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    PlanetApplicationService planetApplicationService;

    @Autowired
    PlanetPrinter planetPrinter;

    @Before
    public void setUp() {
        c00 = MapCoordinate.fromInteger( 0, 0 );
        c10 = MapCoordinate.fromInteger( 1, 0 );
        c20 = MapCoordinate.fromInteger( 2, 0 );
        c01 = MapCoordinate.fromInteger( 0, 1 );
        c11 = MapCoordinate.fromInteger( 1, 1 );
        c21 = MapCoordinate.fromInteger( 2, 1 );

        planetRepository.deleteAll();
        n = new Planet( UUID.randomUUID() );
        n.setSpawnPoint( Boolean.TRUE );
        s = new Planet( UUID.randomUUID() );
        ne = new Planet( UUID.randomUUID() );
        nee = new Planet( UUID.randomUUID() );
        se = new Planet( UUID.randomUUID() );
        see = new Planet( UUID.randomUUID() );
        see.setSpawnPoint( Boolean.TRUE );
        planetRepository.save( n );
        planetRepository.save( s );
        planetRepository.save( ne );
        planetRepository.save( se );
        planetRepository.save( nee );
        planetRepository.save( see );
    }

    private void saveAll() {
        planetRepository.save( n );
        planetRepository.save( s );
        planetRepository.save( ne );
        planetRepository.save( se );
        planetRepository.save( nee );
        planetRepository.save( see );
    }

}
