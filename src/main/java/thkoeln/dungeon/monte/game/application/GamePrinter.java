package thkoeln.dungeon.monte.game.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.util.Printer;
import thkoeln.dungeon.monte.game.domain.Game;
import thkoeln.dungeon.monte.core.util.ConsolePrinter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Printer class to output the current player status to console.
 */
@Service
public class GamePrinter {
    private Logger logger = LoggerFactory.getLogger( GamePrinter.class );
    private GameApplicationService gameApplicationService;
    private List<Printer> printers;


    @Autowired
    public GamePrinter( GameApplicationService gameApplicationService,
                        List<Printer> printers ) {
        this.gameApplicationService = gameApplicationService;
        this.printers = printers;
    }


    public void printStatus() {
        printers.forEach( p -> p.header( "Game" ) );
        Optional<Game> perhapsGame = gameApplicationService.queryActiveGame();
        printers.forEach( p -> p.startLine() );
        if ( !perhapsGame.isPresent() ) {
            printers.forEach( p -> p.write( "No active game found!" ) );
        }
        else {
            printers.forEach( p -> p.write( perhapsGame.get().toString() ) );
        }
        printers.forEach( p -> p.endLine() );
    }
}
