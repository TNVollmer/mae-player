package thkoeln.dungeon.planet.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.domainprimitives.CompassDirection;
import thkoeln.dungeon.domainprimitives.Coordinate;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetDomainService;
import thkoeln.dungeon.planet.domain.PlanetException;
import thkoeln.dungeon.planet.domain.PlanetRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanetApplicationService {
    private Logger logger = LoggerFactory.getLogger( PlanetApplicationService.class );
    private PlanetRepository planetRepository;

    @Autowired
    public PlanetApplicationService( PlanetRepository planetRepository ) {
        this.planetRepository = planetRepository;
    }



}
