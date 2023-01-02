package thkoeln.dungeon.monte.robot.domain;

import thkoeln.dungeon.monte.trading.domain.TradingAccount;

public interface RobotBehavior {

    public void regenerateIfLowAndNotAttacked();

    public void fleeIfAttacked();

    public void mineIfNotMinedLastRound();

    public void mine();

    public void move();

    public void upgrade( TradingAccount tradingAccount );

    public void attack();
}
