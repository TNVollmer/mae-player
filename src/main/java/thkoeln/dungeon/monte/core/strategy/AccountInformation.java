package thkoeln.dungeon.monte.core.strategy;

import thkoeln.dungeon.monte.core.domainprimitives.command.Command;

/**
 * Interface used for dependency injection of the TradingAccount
 */
public interface AccountInformation {

    /**
     * How many robots can I buy, if I invest a share of <shareOfCreditBalance> of my current credit balance
     * (= money in my bank account)?
     * @param shareOfCreditBalance
     * @return
     */
    public int canBuyThatManyRobotsWith( float shareOfCreditBalance  );

    /**
     * Deduce the amount of money that the given command will require from the credit balance
     * @param command
     */
    public void payForCommand( Command command );
}
