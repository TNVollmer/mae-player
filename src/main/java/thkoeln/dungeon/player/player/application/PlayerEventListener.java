package thkoeln.dungeon.player.player.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableItem;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.TradeableType;
import thkoeln.dungeon.player.core.events.AbstractEvent;
import thkoeln.dungeon.player.core.events.EventFactory;
import thkoeln.dungeon.player.core.events.EventHeader;
import thkoeln.dungeon.player.core.events.EventType;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.core.events.concreteevents.trading.BankAccountTransactionBookedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.trading.BankInitializedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.trading.TradeablePricesEvent;
import thkoeln.dungeon.player.robot.application.RobotApplicationService;

import java.util.List;

@Service
public class PlayerEventListener {
    private Logger logger = LoggerFactory.getLogger(PlayerEventListener.class);
    private EventFactory eventFactory;
    private ApplicationEventPublisher applicationEventPublisher;
    private RobotApplicationService robotApplicationService;
    private PlayerApplicationService playerApplicationService;

    @Autowired
    public PlayerEventListener(EventFactory eventFactory,
                               ApplicationEventPublisher applicationEventPublisher, RobotApplicationService robotApplicationService, PlayerApplicationService playerApplicationService
    ) {
        this.eventFactory = eventFactory;
        this.applicationEventPublisher = applicationEventPublisher;
        this.robotApplicationService = robotApplicationService;
        this.playerApplicationService = playerApplicationService;
    }


    /**
     * Listener to all events that the core services send to the player
     *
     * @param eventIdStr
     * @param transactionIdStr
     * @param playerIdStr
     * @param type
     * @param version
     * @param timestampStr
     * @param payload
     */
    @RabbitListener(queues = "player-${dungeon.playerName}")
    public void receiveEvent(@Header(required = false, value = EventHeader.EVENT_ID_KEY) String eventIdStr,
                             @Header(required = false, value = EventHeader.TRANSACTION_ID_KEY) String transactionIdStr,
                             @Header(required = false, value = EventHeader.PLAYER_ID_KEY) String playerIdStr,
                             @Header(required = false, value = EventHeader.TYPE_KEY) String type,
                             @Header(required = false, value = EventHeader.VERSION_KEY) String version,
                             @Header(required = false, value = EventHeader.TIMESTAMP_KEY) String timestampStr,
                             String payload) {
        try {
            EventHeader eventHeader =
                    new EventHeader(type, eventIdStr, playerIdStr, transactionIdStr, timestampStr, version);
            AbstractEvent newEvent = eventFactory.fromHeaderAndPayload(eventHeader, payload);
            if (!eventHeader.getEventType().equals(EventType.TRADABLE_PRICES)){
                logger.info("======== EVENT =====> " + newEvent.toStringShort());
            }
            logger.debug("======== EVENT (detailed) =====>\n" + newEvent);
            if (!newEvent.isValid()) {
                logger.warn("Event invalid: " + newEvent);
                return;
            } else {
                this.applicationEventPublisher.publishEvent(newEvent);
            }
        } catch (Exception e) {
            logger.error("!!!!!!!!!!!!!! EVENT ERROR !!!!!!!!!!!!!\n" + e);
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
            logger.info("Current balance: " + playerApplicationService.queryAndIfNeededCreatePlayer().getBalance());
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
        logger.info("Current prices:");
        logger.info("Mining speed upgrades: " + miningSpeedUpgrades);
        logger.info("Mining upgrades: " + miningUpgrades);
        logger.info("Max energy upgrades: " + maxEnergyUpgrades);
        logger.info("Energy regeneration upgrades: " + energyRegenerationUpgrades);
        logger.info("Health upgrades: " + healthUpgrades);
        logger.info("Storage upgrades: " + storageUpgrades);
        logger.info("Resources: " + resources);
        logger.info("Misc: " + misc);
    }
}
