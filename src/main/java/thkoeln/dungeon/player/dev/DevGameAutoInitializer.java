package thkoeln.dungeon.player.dev;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.core.restadapter.GameDto;
import thkoeln.dungeon.player.game.domain.GameStatus;

import static thkoeln.dungeon.player.dev.DevGameAdminClient.DEV_PREFIX;

@Component
@Profile("dev")
@Slf4j
public class DevGameAutoInitializer {
    private static final int NUMBER_OF_ROUNDS = 10_000;
    private static final int NUMBER_OF_PLAYERS = 10;
    private static final int ROUND_DURATION = 10_000;
    private final DevGameAdminClient devGameAdminClient;


    @Autowired
    public DevGameAutoInitializer(DevGameAdminClient devGameAdminClient) {
        this.devGameAdminClient = devGameAdminClient;
    }


    @Component
    @Profile("dev")
    @Order
    @RequiredArgsConstructor
    public class GameAutoCreator implements InitializingBean {
        @Override
        public void afterPropertiesSet() throws Exception {
            var games = devGameAdminClient.getAllGames();

            // End all existing games
            for (GameDto game : games) {
                if (game.getGameStatus() == GameStatus.CREATED) {
                    devGameAdminClient.startGame(game.getGameId());
                }
                devGameAdminClient.endGame(game.getGameId());
            }
            devGameAdminClient.createGame(NUMBER_OF_ROUNDS, NUMBER_OF_PLAYERS);
        }
    }

    @Component
    @Profile("dev")
    @Order
    @RequiredArgsConstructor
    public class GameAutoStarter implements ApplicationListener<ApplicationReadyEvent> {
        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            var games = devGameAdminClient.getAllGames();
            if (games.size() != 1)
                throw new RuntimeException("Invalid number of games found. That should not happen");
            var game = games.get(0);
            devGameAdminClient.startGame(game.getGameId());
            devGameAdminClient.setRoundDuration(game.getGameId(), ROUND_DURATION);
        }
    }


    @PreDestroy
    public void onExit() {
        // Code here will be executed before the application shuts down
        log.info(DEV_PREFIX + "Application is stopping. Executing onExit() to remove the end the running game.");
        var gameDtos = devGameAdminClient.getAllGames();
        for (GameDto gameDto : gameDtos) {
            if (gameDto.getGameStatus().isActive()) {
                devGameAdminClient.endGame(gameDto.getGameId());
            }
        }
    }

}
