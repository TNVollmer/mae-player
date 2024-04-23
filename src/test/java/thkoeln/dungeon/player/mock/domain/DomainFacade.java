package thkoeln.dungeon.player.mock.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public record DomainFacade(
        ResetDomainFacade resetDomainFacade,
        GameDomainFacade gameDomainFacade,
        PlayerDomainFacade playerDomainFacade,
        TradableDomainFacade tradableDomainFacade,
        PlanetDomainFacade planetDomainFacade,
        RobotDomainFacade robotDomainFacade) {

    @Autowired
    public DomainFacade {

    }

}
