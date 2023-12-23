package thkoeln.dungeon.player;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;

@Component
@RequiredArgsConstructor
public class DungeonPlayerInitializer implements InitializingBean {
    private final PlayerApplicationService playerApplicationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        playerApplicationService.registerPlayer();
    }
}
