package thkoeln.dungeon.monte;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.game.application.GameApplicationService;
import thkoeln.dungeon.monte.player.application.PlayerApplicationService;
import thkoeln.dungeon.monte.player.domain.Player;

@Service
public class DungeonPlayerStartupService implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger( DungeonPlayerStartupService.class );
    private PlayerApplicationService playerApplicationService;
    private GameApplicationService gameApplicationService;

    @Autowired
    public DungeonPlayerStartupService( PlayerApplicationService playerApplicationService,
                                        GameApplicationService gameApplicationService ) {
        this.playerApplicationService = playerApplicationService;
        this.gameApplicationService = gameApplicationService;
    }

    /**
     * In this method, the player participation is prepared. If there are problems (connection
     * problems, no running game, etc.) the player waits 10s and tries again.
     * @param event
     */
    @Override
    public void onApplicationEvent( ContextRefreshedEvent event ) {
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        if ( !player.hasJoinedGame() ) {
            try {
                gameApplicationService.fetchRemoteGame();
                playerApplicationService.registerPlayer();
                playerApplicationService.letPlayerJoinOpenGame();
            } catch ( DungeonPlayerRuntimeException exc ) {
                logger.error( "Error when initializing player: " + exc.getMessage() );
            }
        }
    }
}
