package thkoeln.dungeon.monte.player.domain;

import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.command.Command;
import thkoeln.dungeon.monte.trading.application.CommandShop;

@Service
public class PlayerDefaultStrategy implements PlayerStrategy {
    private CommandShop commandShop;

    public static final float MONEY_SHARE_FOR_NEW_ROBOTS = 0.5f;

    @Override
    public Command purchaseRobotsCommand() {
        //Command purchaseNewRobotCommand = commandShop.purchase( ROBOT, MONEY_SHARE_FOR_NEW_ROBOTS );
        //return purchaseNewRobotCommand;
        return null;
    }

}
