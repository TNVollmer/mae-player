package thkoeln.dungeon.player.robot.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.player.domain.Player;

@Service
public class StrategyService {

    private PlayerApplicationService playerApplicationService;


    @Autowired
    public StrategyService(PlayerApplicationService playerApplicationService) {
        this.playerApplicationService = playerApplicationService;
    }

    public void runCommands(){
        Player player = playerApplicationService.queryAndIfNeededCreatePlayer();
    }
}
