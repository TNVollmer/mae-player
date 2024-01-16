package thkoeln.dungeon.player.dev;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;

import java.util.Map;

import static thkoeln.dungeon.player.dev.DevGameAdminClient.DEV_PREFIX;

@Component
@Profile("dev")
@Order
@RequiredArgsConstructor
@Slf4j
public class DevDungeonPlayerInitializer implements InitializingBean {
    private final DevGameAdminClient devGameAdminClient;
    private final PlayerApplicationService playerApplicationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug( DEV_PREFIX + "Initializer: This is my environment." );
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            log.debug(DEV_PREFIX + "   -- Environment variable: " + envName + " = " + env.get(envName));
        }

        log.info( DEV_PREFIX + "Initializer: Register player." );
        playerApplicationService.registerPlayer();

        log.info( DEV_PREFIX + "Initializer: Create a game." );
        devGameAdminClient.createGameInDevMode();

        log.info( DEV_PREFIX + "Initializer: Game created. Player will join as reaction to CREATED event." );
    }

    @PreDestroy
    public void onExit() {
        // Code here will be executed before the application shuts down
        log.info( DEV_PREFIX + "Application is stopping. Executing onExit() to remove the end the running game." );
        devGameAdminClient.endAllGames();
    }
}
