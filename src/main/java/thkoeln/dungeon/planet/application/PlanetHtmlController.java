package thkoeln.dungeon.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

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