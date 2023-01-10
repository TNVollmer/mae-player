package thkoeln.dungeon.monte.game.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.printer.OutputDevice;
import thkoeln.dungeon.monte.game.domain.Game;

import java.util.List;
import java.util.Optional;

/**
 * OutputDevice class to output the current player status to console.
 */
@Service
public class GamePrinter {
    private Logger logger = LoggerFactory.getLogger( GamePrinter.class );
    private GameApplicationService gameApplicationService;
    private List<OutputDevice> outputDevices;


    @Autowired
    public GamePrinter( GameApplicationService gameApplicationService,
                        List<OutputDevice> outputDevices) {
        this.gameApplicationService = gameApplicationService;
        this.outputDevices = outputDevices;
    }


    public void printStatus() {
        outputDevices.forEach(p -> p.header( "Game" ) );
        Optional<Game> perhapsGame = gameApplicationService.queryActiveGame();
        outputDevices.forEach(p -> p.startLine() );
        if ( !perhapsGame.isPresent() ) {
            outputDevices.forEach(p -> p.write( "No active game found!" ) );
        }
        else {
            outputDevices.forEach(p -> p.write( perhapsGame.get().toString() ) );
        }
        outputDevices.forEach(p -> p.endLine() );
    }
}
