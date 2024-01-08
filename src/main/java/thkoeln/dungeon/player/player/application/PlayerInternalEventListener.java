package thkoeln.dungeon.player.player.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableItem;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableType;
import thkoeln.dungeon.player.core.events.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.core.events.concreteevents.trading.BankAccountTransactionBookedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.trading.BankInitializedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.trading.TradeablePricesEvent;
import thkoeln.dungeon.player.game.application.GameApplicationService;
import thkoeln.dungeon.player.game.domain.GameStatus;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerInternalEventListener {
    private final GameApplicationService gameApplicationService;
    private final PlayerApplicationService playerApplicationService;
    private final PlayerGameAutoStarter playerGameAutoStarter;
    private final Logger logger = LoggerFactory.getLogger( PlayerExternalEventListener.class);
    private final String loggerName = "PlayerInternalEventListener --> ";

    @EventListener( GameStatusEvent.class )
    void handleGameStatusEvent( GameStatusEvent gameStatusEvent ) {
        if ( GameStatus.CREATED.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.fetchRemoteGame();
            playerApplicationService.letPlayerJoinOpenGame();
            // this is relevant for the dev profile only - in production, the game will be started
            // by the game admin, and this interface is just an empty method call.
            playerGameAutoStarter.startGame();
        }
        if ( GameStatus.ENDED.equals( gameStatusEvent.getStatus() ) ) {
            playerApplicationService.cleanupAfterFinishingGame();
        }
    }
    @EventListener(BankInitializedEvent.class)
    private void initialiseMoney(BankInitializedEvent bankInitializedEvent) {
        playerApplicationService.updateMoney(Money.from(bankInitializedEvent.getBalance()));
    }

    @EventListener(BankAccountTransactionBookedEvent.class)
    private void updateMoney(BankAccountTransactionBookedEvent bankAccountTransactionBookedEvent) {
        playerApplicationService.updateMoney(Money.from(bankAccountTransactionBookedEvent.getBalance()));
    }

    @EventListener(RoundStatusEvent.class)
    private void displayBalance(RoundStatusEvent roundStatusEvent) {
        if (roundStatusEvent.getRoundStatus().equals(RoundStatusType.STARTED)) {
            logger.info(loggerName + "Current balance: " + playerApplicationService.queryAndIfNeededCreatePlayer().getBalance());
        }
    }

    //@EventListener(TradeablePricesEvent.class)
    private void displayPrices(TradeablePricesEvent tradeablePricesEvent) {
        List<TradeableItem> items = tradeablePricesEvent.getTradeableItems();
        StringBuilder miningUpgrades = new StringBuilder();
        StringBuilder miningSpeedUpgrades = new StringBuilder();
        StringBuilder maxEnergyUpgrades = new StringBuilder();
        StringBuilder energyRegenerationUpgrades = new StringBuilder();
        StringBuilder healthUpgrades = new StringBuilder();
        StringBuilder storageUpgrades = new StringBuilder();
        StringBuilder damageUpgrades = new StringBuilder();
        StringBuilder resources = new StringBuilder();
        StringBuilder misc = new StringBuilder();
        for (TradeableItem item : items) {
            if (item.getName().contains("MINING_SPEED")) {
                miningSpeedUpgrades.append(item.getName()).append(": ").append(item.getPrice()).append(" | ");
            } else if (item.getName().contains("MINING")) {
                miningUpgrades.append(item.getName()).append(": ").append(item.getPrice()).append(" | ");
            } else if (item.getName().contains("MAX_ENERGY")) {
                maxEnergyUpgrades.append(item.getName()).append(": ").append(item.getPrice()).append(" | ");
            } else if (item.getName().contains("ENERGY_REGEN")) {
                energyRegenerationUpgrades.append(item.getName()).append(": ").append(item.getPrice()).append(" | ");
            } else if (item.getName().contains("HEALTH")) {
                healthUpgrades.append(item.getName()).append(": ").append(item.getPrice()).append(" | ");
            } else if (item.getName().contains("STORAGE")) {
                storageUpgrades.append(item.getName()).append(": ").append(item.getPrice()).append(" | ");
            } else if (item.getName().contains("DAMAGE")) {
                damageUpgrades.append(item.getName()).append(": ").append(item.getPrice()).append(" | ");
            } else if (item.getType().equals(TradeableType.RESOURCE)) {
                resources.append(item.getName()).append(": ").append(item.getPrice()).append(" | ");
            } else {
                misc.append(item.getName()).append(": ").append(item.getPrice()).append(" | ");
            }
        }
        logger.info(loggerName + "Current prices:");
        logger.info(loggerName + "Mining speed upgrades: " + miningSpeedUpgrades);
        logger.info(loggerName + "Mining upgrades: " + miningUpgrades);
        logger.info(loggerName + "Max energy upgrades: " + maxEnergyUpgrades);
        logger.info(loggerName + "Energy regeneration upgrades: " + energyRegenerationUpgrades);
        logger.info(loggerName + "Health upgrades: " + healthUpgrades);
        logger.info(loggerName + "Storage upgrades: " + storageUpgrades);
        logger.info(loggerName + "Resources: " + resources);
        logger.info(loggerName + "Misc: " + misc);
    }

    @EventListener(TradeablePricesEvent.class)
    private void updatePrices(TradeablePricesEvent tradeablePricesEvent) {
        playerApplicationService.updatePrices(tradeablePricesEvent.getTradeableItems());
    }

}
