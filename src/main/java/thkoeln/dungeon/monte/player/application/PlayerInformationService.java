package thkoeln.dungeon.monte.player.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.util.PlayerInformation;
import thkoeln.dungeon.monte.game.application.GameApplicationService;
import thkoeln.dungeon.monte.game.domain.Game;
import thkoeln.dungeon.monte.game.domain.GameRepository;
import thkoeln.dungeon.monte.game.domain.GameStatus;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.player.domain.Player;
import thkoeln.dungeon.monte.player.domain.PlayerRepository;

import java.util.List;
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
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;


    @Autowired
    public PlayerInformationService( PlayerRepository playerRepository,
                                     GameRepository gameRepository ) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }


    @Override
    public UUID currentGameId() {
        Optional<Game> perhapsGame = gameRepository.findFirstByGameStatusEquals( GameStatus.RUNNING );
        return perhapsGame.isPresent() ? perhapsGame.get().getGameId() : null;
    }

    @Override
    public UUID currentPlayerId() {
        List<Player> players = playerRepository.findAll();
        return players.size() >= 1 ? players.get( 0 ).getPlayerId() : null;
    }
}
