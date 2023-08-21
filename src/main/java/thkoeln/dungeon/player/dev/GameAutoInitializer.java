package thkoeln.dungeon.player.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.core.restadapter.GameDto;
import thkoeln.dungeon.player.game.domain.GameStatus;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class GameAutoInitializer {
  private static final int NUMBER_OF_ROUNDS = 10_000;
  private static final int NUMBER_OF_PLAYERS = 10;
  private static final int ROUND_DURATION = 10_000;
  private final GameAdminClient gameAdminClient;

  @Component
  @Profile("dev")
  @Order
  @RequiredArgsConstructor
  public class GameAutoCreator implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
      var games = gameAdminClient.getAllGames();

      // End all existing games
      for (GameDto game : games) {
        if(game.getGameStatus() == GameStatus.CREATED) {
          gameAdminClient.startGame(game.getGameId());
        }
        gameAdminClient.endGame(game.getGameId());
      }

      gameAdminClient.createGame(NUMBER_OF_ROUNDS, NUMBER_OF_PLAYERS);
    }
  }

  @Component
  @Profile("dev")
  @Order
  @RequiredArgsConstructor
  public class GameAutoStarter implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
      var games = gameAdminClient.getAllGames();
      if(games.size() != 1) throw new RuntimeException("Invalid number of games found. That should not happen");
      var game = games.get(0);
      gameAdminClient.startGame(game.getGameId());

      gameAdminClient.setRoundDuration(game.getGameId(), ROUND_DURATION);
    }
  }

}
