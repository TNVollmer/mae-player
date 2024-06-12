package thkoeln.dungeon.player.trading.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.core.events.concreteevents.trading.TradableBoughtEvent;
import thkoeln.dungeon.player.core.events.concreteevents.trading.TradablePricesEvent;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.robot.domain.Shop;

@Service
public class TradingApplicationService {

    private static final Logger log = LoggerFactory.getLogger(TradingApplicationService.class);
    private final PlayerRepository playerRepository;

    @Autowired
    public TradingApplicationService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Async
    @EventListener(TradablePricesEvent.class)
    public void updateTradePrices(TradablePricesEvent event) {
        Shop.updateItems(event.getTradeableItems());
        log.info("Updated prices or not...");
    }

    @Async
    @EventListener(TradableBoughtEvent.class)
    public void onTradableBoughtEvent(TradableBoughtEvent event) {
        Player player = playerRepository.findAll().get(0);
        switch (event.getType()) {
            case "ITEM":
                player.decreaseRobotBudget(Money.from(event.getTotalPrice()));
                break;
            case "UPGRADE":
                player.decreaseUpgradeBudget(Money.from(event.getTotalPrice()));
                break;
            case "RESTORATION":
                player.decreaseMiscBudget(Money.from(event.getTotalPrice()));
                break;
        }
        playerRepository.save(player);
        if (event.getRobotId() == null)
            log.info("Bought {} Robots for {}", event.getAmount(), event.getTotalPrice());
        else
            log.info("{} bought: {} ({}) for {}", event.getRobotId(), event.getName(), event.getType(), event.getTotalPrice());

        log.info("Bank account updated to {} money.", player.getBankAccount());
        log.info("Upgrade Budget: {}", player.getUpgradeBudget());
        log.info("New Robots Budget: {}", player.getNewRobotsBudget());
        log.info("New Misc Budget: {}", player.getMiscBudget());
    }
}
