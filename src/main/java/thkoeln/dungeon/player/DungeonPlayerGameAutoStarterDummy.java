package thkoeln.dungeon.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.player.application.PlayerGameAutoStarter;


@Component
@Profile("!dev")
@Order
@Slf4j
@RequiredArgsConstructor
public class DungeonPlayerGameAutoStarterDummy implements PlayerGameAutoStarter {

    @Override
    public void startGame() {
        log.debug( "DungeonPlayerGameAutoStarterDummy: dummy starter method called." );
    }
}
