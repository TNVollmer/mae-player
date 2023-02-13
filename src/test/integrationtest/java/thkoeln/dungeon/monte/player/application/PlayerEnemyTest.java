package thkoeln.dungeon.monte.player.application;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import thkoeln.dungeon.monte.DungeonPlayerConfiguration;
import thkoeln.dungeon.monte.core.AbstractDungeonMockingTest;
import thkoeln.dungeon.monte.game.application.GameApplicationService;
import thkoeln.dungeon.monte.game.domain.Game;
import thkoeln.dungeon.monte.game.domain.GameRepository;
import thkoeln.dungeon.monte.player.domain.Player;
import thkoeln.dungeon.monte.player.domain.PlayerRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class PlayerEnemyTest extends AbstractDungeonMockingTest {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerApplicationService playerApplicationService;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        playerRepository.deleteAll();
    }


    @Test
    public void testAddEnemyPlayers() throws Exception {
        // given
        Player meMyselfAndI = playerApplicationService.queryAndIfNeededCreatePlayer();
        UUID myPlayerId = UUID.randomUUID();
        String myShortName = myPlayerId.toString().substring( 0, 8 );
        meMyselfAndI.assignPlayerId( myPlayerId );
        playerRepository.save( meMyselfAndI );

        // when
        Player enemyA = playerApplicationService.addEnemyPlayer( "aaaa0000" );
        Player enemyB = playerApplicationService.addEnemyPlayer( "bbbb0000" );
        Player enemyB1 = playerApplicationService.addEnemyPlayer( "bbbb0000" );
        Player enemyC = playerApplicationService.addEnemyPlayer( "cccc0000" );
        Player myselfButShouldBeNull = playerApplicationService.addEnemyPlayer( myShortName );

        // then
        assertTrue( enemyA.matchesShortName( "aaaa0000" ) );
        assertTrue( enemyB.matchesShortName( "bbbb0000" ) );
        assertTrue( enemyB1.matchesShortName( "bbbb0000" ) );
        assertTrue( enemyC.matchesShortName( "cccc0000" ) );
        assertNull( myselfButShouldBeNull );
        assertEquals( enemyB, enemyB1 );
        assertEquals( 'A', enemyA.getEnemyChar() );
        assertEquals( 'B', enemyB.getEnemyChar() );
        assertEquals( 'B', enemyB1.getEnemyChar() );
        assertEquals( 'C', enemyC.getEnemyChar() );
        assertNull( meMyselfAndI.getEnemyChar() );
    }


}
