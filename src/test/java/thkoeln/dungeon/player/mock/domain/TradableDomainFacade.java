package thkoeln.dungeon.player.mock.domain;

import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableType;

import java.util.List;

public interface TradableDomainFacade {

    /**
     * @param <T>
     * @return a list of all tradable items (usually obtained at the beginning of a game through the tradable prices event)
     */
    public <T> List<T> getAllTradableItems();

    /**
     * @param name
     * @param <T>
     * @return the tradable item having the given name, or null if you dont find any
     */
    public <T> T getTradableItemByName(String name);

    /**
     * @param tradableItem
     * @param <T>
     * @return the money price of the given tradable
     */
    public <T> Integer getPriceOfTradableItem(T tradableItem);

    /**
     * @param tradableItem
     * @param <T>
     * @return the tradable type of the given tradable
     */
    public <T> TradeableType getTradableTypeOfTradableItem(T tradableItem);

}
