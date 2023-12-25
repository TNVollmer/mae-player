package thkoeln.dungeon.player.dev;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static thkoeln.dungeon.player.dev.DevGameAdminClient.DEV_PREFIX;

@Component
@Profile("dev")
@Order
@RequiredArgsConstructor
@Slf4j
public class DevDungeonPlayerStartupService implements ApplicationListener<ApplicationReadyEvent> {
    private final DevGameAdminClient devGameAdminClient;

    @Override
    public void onApplicationEvent( ApplicationReadyEvent event ) {
        log.info( DEV_PREFIX + "onApplicationEvent() - start game." );
        devGameAdminClient.cleanUpAndCreateGame();
    }


    @PreDestroy
    public void onExit() {
        // Code here will be executed before the application shuts down
        log.info( DEV_PREFIX + "Application is stopping. Executing onExit() to remove the end the running game." );
        devGameAdminClient.endAllGames();
    }
}
