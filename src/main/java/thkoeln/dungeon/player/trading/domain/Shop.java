package thkoeln.dungeon.player.trading.domain;

import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableItem;

import java.util.ArrayList;
import java.util.List;

public class Shop {

    private static final List<TradeableItem> wares = new ArrayList<>();

    public static void updateItems(List<TradeableItem> items) {
        if (!wares.isEmpty()) return;
        wares.addAll(items);
    }

    public static Money getPriceForItem(String name) {
        for (TradeableItem item : wares) {
            if (item.getName().equals(name))
                return item.getPrice();
        }
        return null;
    }
}
