package thkoeln.dungeon.player.robot.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.Player;

@Service
public class StrategyService {

    private PlayerApplicationService playerApplicationService;


    @Autowired
    public StrategyService(PlayerApplicationService playerApplicationService) {
        this.playerApplicationService = playerApplicationService;
    }

    @EventListener(RoundStatusEvent.class)
    public void runCommands(RoundStatusEvent roundStatusEvent){
        if (!roundStatusEvent.getRoundStatus().equals("started")){return;}
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
    }
}
