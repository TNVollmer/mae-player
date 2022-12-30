package thkoeln.dungeon.monte.planet.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.monte.domainprimitives.CompassDirection;
import thkoeln.dungeon.monte.planet.domain.Planet;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PlanetPersistenceTest {
    private Planet[][] planetArray= new Planet[3][3];
    private Integer[][] numberOfNeighbours = new Integer[][] {{2, 3, 2}, {3, 4, 3}, {2, 3, 2}};

    @Autowired
    private PlanetApplicationService planetApplicationService;

    @BeforeEach
    public void setup() {
        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                planetArray[i][j] = new Planet();
            }
        }
    }


    @Test
    public void testPersistMixOfPersistentAndTransient() {
        // given
        planetApplicationService.save( planetArray[1][1] );

        // when
        planetArray[1][2].defineNeighbour( planetArray[1][1], CompassDirection.WEST );
        planetApplicationService.save( planetArray[1][1] );
        planetApplicationService.save( planetArray[1][2] );
        Planet p11 = planetArray[1][1];
        Planet p12 = planetArray[1][2];

        // then
        List<Planet> persistentPlanets = planetApplicationService.allPlanets();
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
                if ( i < 2 ) planetArray[i][j].defineNeighbour( planetArray[i+1][j], CompassDirection.EAST );
                if ( j < 2 ) planetArray[i][j].defineNeighbour( planetArray[i][j+1], CompassDirection.SOUTH );
            }
        }

        // when
        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                planetApplicationService.save( planetArray[i][j] );
            }
        }

        // then
        for( int i = 0; i<=2; i++ ) {
            for (int j = 0; j <= 2; j++) {
                Planet planet = planetApplicationService.findById( planetArray[i][j].getId() ).get();
                assertEquals( numberOfNeighbours[i][j], planet.allNeighbours().size() );
            }
        }
    }

}
