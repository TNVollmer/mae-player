package thkoeln.dungeon.planet.application;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.domainprimitives.CompassDirection;
import thkoeln.dungeon.domainprimitives.TwoDimDynamicArray;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;

import java.util.Map;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlanetApplicationServiceTest {
    private Planet n, s, ne, se, nee, see;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    PlanetApplicationService planetApplicationService;

    @Before
    public void setUp() {
        planetRepository.deleteAll();
        n = new Planet( "n" );
        n.setSpacestation( TRUE );
        s = new Planet( "s" );
        ne = new Planet( "ne" );
        nee = new Planet( "nee" );
        se = new Planet( "se" );
        see = new Planet( "see" );
        see.setSpacestation( TRUE );
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

    /*
    @Test
    public void testOneIsland() {
        // given
        n.defineNeighbour( s, CompassDirection.SOUTH );
        n.defineNeighbour( ne, CompassDirection.EAST );
        ne.defineNeighbour( nee, CompassDirection.EAST );
        s.defineNeighbour( se, CompassDirection.EAST );
        se.defineNeighbour( see, CompassDirection.EAST );
        //    N--NE--NEE
        //    |
        //    S--SE--SEE

        // when
        Map<Planet, TwoDimDynamicArray<Planet>> planetMap = planetApplicationService.allPlanetsAs2DArrays();

        // then
        System.out.println( planetMap.get( n ).toString() );
        assertEquals( 1, planetMap.size() );
        assertEquals( 2, planetMap.get( n ).sizeY() );
        assertEquals( 3, planetMap.get( n ).sizeX() );
    }

    */
    
    @Test
    public void testClosingCycles() {
        // given

        // when
        n.defineNeighbour( s, CompassDirection.SOUTH );
        //    N
        //    |
        //    S

        n.defineNeighbour( ne, CompassDirection.EAST );
        //    N--NE
        //    |
        //    S

        ne.defineNeighbour( nee, CompassDirection.EAST );
        //    N--NE--NEE
        //    |
        //    S

        s.defineNeighbour( se, CompassDirection.EAST );
        //    N--NE--NEE
        //    |
        //    S--SE

        se.defineNeighbour( see, CompassDirection.EAST );
        //    N--NE--NEE
        //    |
        //    S--SE--SEE

        // then
        assertEquals( nee,
                s.getNorthNeighbour().
                        getEastNeighbour().
                        getSouthNeighbour().
                        getEastNeighbour().
                        getNorthNeighbour() );
    }



}
