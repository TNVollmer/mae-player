package thkoeln.dungeon.player.mock.domain;

public interface PlayerDomainFacade {

    /**
     * @param player
     * @param <T>
     * @return the money balance of the given player
     */
    public <T> Integer getBalanceOfPlayer(T player);

    /**
     * Set the money balance for the given player
     * @param player
     * @param balance
     * @param <T>
     */
    public <T> void setBalanceForPlayer(T player, int balance);

}
