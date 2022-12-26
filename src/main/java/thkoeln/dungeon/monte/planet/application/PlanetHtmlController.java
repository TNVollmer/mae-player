package thkoeln.dungeon.monte.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlanetHtmlController {
    private Logger logger = LoggerFactory.getLogger(PlanetHtmlController.class);

    @GetMapping("/map")
    public String main( Model model ) {
        logger.info( "called /map" );
        model.addAttribute("message", "jdsajdlskajdlaksj");

        return "map"; //view
    }
}