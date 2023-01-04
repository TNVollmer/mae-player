package thkoeln.dungeon.monte.core.strategy;

public interface AccountInformation {
    /**
     * How many robots can I buy, if I invest a share of <shareOfCreditBalance> of my current credit balance
     * (= money in my bank account)?
     * @param shareOfCreditBalance
     * @return
     */
    public int canBuyThatManyRobotsWith( float shareOfCreditBalance  );
}
