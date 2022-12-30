package thkoeln.dungeon.monte.game.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.game.domain.Game;
import thkoeln.dungeon.monte.util.AbstractPrinter;

import java.util.Optional;

/**
 * Printer class to output the current player status to console.
 */
@Service
public class GamePrinter extends AbstractPrinter {
    private Logger logger = LoggerFactory.getLogger( GamePrinter.class );
    private GameApplicationService gameApplicationService;

    @Autowired
    public GamePrinter( GameApplicationService gameApplicationService ) {
        this.gameApplicationService = gameApplicationService;
    }


    public void printStatus() {
        Optional<Game> perhapsGame = gameApplicationService.queryActiveGame();
        if ( !perhapsGame.isPresent() ) {
            writeLine( "No active game found!" );
        }
        else {
            writeLine( perhapsGame.get().toString() );
        }
    }
}
