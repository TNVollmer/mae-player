package thkoeln.dungeon.player;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.game.application.GameApplicationService;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.Player;

@Service
@Profile("!dev")
public class DungeonPlayerStartupService implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(DungeonPlayerStartupService.class);
    private final PlayerApplicationService playerApplicationService;
    private final GameApplicationService gameApplicationService;

    @Autowired
    public DungeonPlayerStartupService(PlayerApplicationService playerApplicationService,
                                       GameApplicationService gameApplicationService) {
        this.playerApplicationService = playerApplicationService;
        this.gameApplicationService = gameApplicationService;
    }

    /**
     * In this method, the player participation is prepared. If there are problems (connection
     * problems, no running game, etc.) the player waits 10s and tries again.
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
        try {
            gameApplicationService.fetchRemoteGame();
            playerApplicationService.pollForOpenGame();
        } catch (DungeonPlayerRuntimeException exc) {
            logger.error("Error when initializing player: " + exc.getMessage());
        }
    }
}
