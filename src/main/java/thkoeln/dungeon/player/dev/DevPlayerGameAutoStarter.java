package thkoeln.dungeon.player.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.player.application.PlayerGameAutoStarter;

import static thkoeln.dungeon.player.dev.DevGameAdminClient.DEV_PREFIX;

@Component
@Slf4j
@Profile("dev")
@Order
@RequiredArgsConstructor
public class DevPlayerGameAutoStarter implements PlayerGameAutoStarter {
    private final DevGameAdminClient devGameAdminClient;

    @Override
    public void startGame() {
        log.info( DEV_PREFIX + "DevPlayerGameAutoStarter: start a game." );
        devGameAdminClient.startGameInDevMode();
    }
}
