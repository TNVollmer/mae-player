package thkoeln.dungeon.planet.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import thkoeln.dungeon.domainprimitives.CompassDirection;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;

import javax.transaction.Transactional;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlanetTest {
    private Planet[][] planetArray= new Planet[3][3];
    private Integer[][] numberOfNeighbours = new Integer[][] {{2, 3, 2}, {3, 4, 3}, {2, 3, 2}};

    @Autowired
    private PlanetRepository planetRepository;

    @BeforeEach
    public void setup() {
        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                planetArray[i][j] = new Planet();
            }
        }
    }

    @Test
    public void testNeighbouringSetterGetterViaReflection() throws Exception {
        // given
        Planet planet = new Planet();

        // when
        Method getter = planet.neighbouringGetter( CompassDirection.SOUTH);
        Method setter = planet.neighbouringSetter( CompassDirection.WEST);

        // then
        assertEquals( "getSouthNeighbour", getter.getName() );
        assertEquals( "setWestNeighbour", setter.getName() );
    }

    @Test
    public void testEstablishNeighbouringRelationship() {
        // given
        // when
        planetArray[0][1].defineNeighbour( planetArray[1][1], CompassDirection.EAST);
        planetArray[0][1].defineNeighbour( planetArray[0][2], CompassDirection.NORTH);

        // then
        assertEquals( planetArray[1][1], planetArray[0][1].getEastNeighbour() );
        assertEquals( planetArray[0][1], planetArray[1][1].getWestNeighbour() );
        assertEquals( planetArray[0][2], planetArray[0][1].getNorthNeighbour() );
        assertEquals( planetArray[0][1], planetArray[0][2].getSouthNeighbour() );
    }


    @Test
    public void testPersistMixOfPersistentAndTransient() {
        // given
        planetRepository.save( planetArray[1][1] );

        // when
        planetArray[1][2].defineNeighbour( planetArray[1][1], CompassDirection.WEST);
        planetRepository.save( planetArray[1][1] );
        planetRepository.save( planetArray[1][2] );
        Planet p11 = planetArray[1][1];
        Planet p12 = planetArray[1][2];

        // then
        List<Planet> persistentPlanets = planetRepository.findAll();
        assertEquals( 2, persistentPlanets.size() );
        assertEquals( p11, p12.getWestNeighbour() );
        assertEquals( p12, p11.getEastNeighbour() );
    }

    @Test
    @Transactional
    public void testSaveAllNeighboursAtOnce() {
        // given
        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                if ( i < 2 ) planetArray[i][j].defineNeighbour( planetArray[i+1][j], CompassDirection.EAST);
                if ( j < 2 ) planetArray[i][j].defineNeighbour( planetArray[i][j+1], CompassDirection.SOUTH);
            }
        }

        // when
        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                planetRepository.save( planetArray[i][j] );
            }
        }

        // then
        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                Planet planet = planetRepository.findById( planetArray[i][j].getId() ).get();
                assertEquals( numberOfNeighbours[i][j], planet.allNeighbours().size() );
            }
        }
    }


    @Test
    public void testClosingCycles() {
        // given
        Planet n = new Planet();
        Planet s = new Planet();
        Planet ne = new Planet();
        Planet nee = new Planet();
        Planet se = new Planet();
        Planet see = new Planet();

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
