package thkoeln.dungeon.monte.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.robot.domain.Robot;
import thkoeln.dungeon.monte.util.AbstractPrinter;

import java.util.List;

/**
 * Printer class to output the current player status to console.
 */
@Service
public class PlanetPrinter extends AbstractPrinter {
    private Logger logger = LoggerFactory.getLogger( PlanetPrinter.class );
    private PlanetApplicationService planetApplicationService;

    @Autowired
    public PlanetPrinter( PlanetApplicationService planetApplicationService ) {
        this.planetApplicationService = planetApplicationService;
    }



    /**
     * @return Print all currently alive robots, in a compact format suitable for the console.
     */

    public void printPlanetList() {
        writeLine( "Known planets:" );
        List<Planet> planets = planetApplicationService.findAll();
        for ( Planet planet : planets) {
            writeLineIndent( planet.toStringDetailed() );
        }
    }



}
