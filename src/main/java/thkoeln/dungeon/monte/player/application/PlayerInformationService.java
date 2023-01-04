package thkoeln.dungeon.monte.player.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.util.PlayerInformation;
import thkoeln.dungeon.monte.game.application.GameApplicationService;
import thkoeln.dungeon.monte.game.domain.Game;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;

import java.util.Optional;
import java.util.UUID;

/**
 * Used to obtain general player information that are needed e.g. for creating commands.
 * We use a Dependency Inversion pattern here: Modules can autowire the interface,
 * while this implementation is contributed by Player. Without Dependency Inversion,
 * this would cause cycles.
 */
@Service
public class PlayerInformationService implements PlayerInformation {
    private PlayerApplicationService playerApplicationService;
    private GameApplicationService gameApplicationService;


    @Autowired
    public PlayerInformationService( PlayerApplicationService playerApplicationService,
                                     GameApplicationService gameApplicationService ) {
        this.playerApplicationService = playerApplicationService;
        this.gameApplicationService = gameApplicationService;
    }


    @Override
    public UUID currentGameId() {
        Optional<Game> perhapsGame = gameApplicationService.queryActiveGame();
        if ( perhapsGame.isPresent() ) return perhapsGame.get().getGameId();
        return null;
    }

    @Override
    public UUID currentPlayerId() {
        return playerApplicationService.queryAndIfNeededCreatePlayer().getPlayerId();
    }
}
