package thkoeln.dungeon.player.player.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Capability;
import thkoeln.dungeon.player.core.domainprimitives.purchasing.Money;
import thkoeln.dungeon.player.core.events.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.core.events.concreteevents.trading.BankAccountTransactionBookedEvent;
import thkoeln.dungeon.player.core.events.concreteevents.trading.BankInitializedEvent;
import thkoeln.dungeon.player.core.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.player.game.application.GameApplicationService;
import thkoeln.dungeon.player.game.domain.Game;
import thkoeln.dungeon.player.player.domain.Player;
import thkoeln.dungeon.player.player.domain.PlayerRepository;
import thkoeln.dungeon.player.robot.domain.Robot;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

import java.util.List;

import static thkoeln.dungeon.player.game.domain.GameStatus.CREATED;

/**
 * This game class encapsulates the game tactics for a simple autonomous controlling of a robot
 * swarm. It has the following structure:
 * - the "round started" event triggers the main round() method
 * - if there is enough money, new robots are bought (or, depending on configuration, existing robots are upgraded)
 * - for each robot, the proper command is chosen and issued (based on the configured tactics)
 * - each time an answer is received (with transaction id), the robots and the map are updated.
 */
@Service
public class PlayerApplicationService {
    private final Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);
    private final PlayerRepository playerRepository;
    private final GameApplicationService gameApplicationService;
    private final GameServiceRESTAdapter gameServiceRESTAdapter;
    private final RobotRepository robotRepository;
    PlayerGameAutoStarter playerGameAutoStarter;


    @Value("${dungeon.playerName}")
    private String playerName;

    @Value("${dungeon.playerEmail}")
    private String playerEmail;

    @Autowired
    public PlayerApplicationService(
            PlayerRepository playerRepository,
            GameApplicationService gameApplicationService,
            GameServiceRESTAdapter gameServiceRESTAdapter,
            PlayerGameAutoStarter playerGameAutoStarter, RobotRepository robotRepository)
    {
        this.playerRepository = playerRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.gameApplicationService = gameApplicationService;
        this.playerGameAutoStarter = playerGameAutoStarter;
        this.robotRepository = robotRepository;
    }


    @EventListener( GameStatusEvent.class )
    @Order(1)
    void joinNewlyCreatedGame( GameStatusEvent gameStatusEvent ) {
        if ( !CREATED.equals( gameStatusEvent.getStatus() ) ) return;
        gameApplicationService.fetchRemoteGame();
        letPlayerJoinOpenGame();
        // this is relevant for the dev profile only - in production, the game will be started
        // by the game admin, and this interface is just an empty method call.
        playerGameAutoStarter.startGame();
    }

    /**
     * Fetch the existing player. If there isn't one yet, it is created and stored to the database.
     *
     * @return The current player.
     */
    public Player queryAndIfNeededCreatePlayer() {
        Player player = null;
        List<Player> players = playerRepository.findAll();
        if (players.size() >= 1) {
            player = players.get(0);
        } else {
            player = Player.ownPlayer(playerName, playerEmail);
            playerRepository.save(player);
            logger.info("Created new player (not yet registered): " + player);
        }
        return player;
    }


    /**
     * Register the current player (or do nothing, if it is already registered)
     */
    public void registerPlayer() {
        Player player = queryAndIfNeededCreatePlayer();
        if (player.getPlayerId() != null) {
            logger.info("Player " + player + " is already registered.");
            return;
        }
        var remotePlayer = gameServiceRESTAdapter.sendGetRequestForPlayerId(player.getName(), player.getEmail());
        if (remotePlayer == null) {
            remotePlayer = gameServiceRESTAdapter.sendPostRequestForPlayerId(player.getName(), player.getEmail());
        }
        if (remotePlayer == null) {
            logger.warn("Registration for player " + player + " failed.");
            return;
        }
        player.assignPlayerId(remotePlayer.getPlayerId());
        player.setPlayerExchange(remotePlayer.getPlayerExchange());
        player.setPlayerQueue(remotePlayer.getPlayerQueue());
        Game activeGame = gameApplicationService.queryActiveGame();
        if (activeGame != null) player.setGameId(gameApplicationService.queryActiveGame().getGameId());
        playerRepository.save(player);
        logger.info("PlayerId sucessfully obtained for " + player + ", is now registered.");
    }


    /**
     * Check if our player is not currently in a game, and if so, let him join the game -
     * if there is one, and it is open.
     *
     * @return True, if the player joined a game, false otherwise.
     */
    public boolean letPlayerJoinOpenGame() {
        logger.info("Trying to join game ...");
        Player player = queryAndIfNeededCreatePlayer();
        Game activeGame = gameApplicationService.queryAndIfNeededFetchRemoteGame();
        if (activeGame == null) {
            logger.info("No open game at the moment - cannot join a game.");
            return false;
        }
        if (!activeGame.getOurPlayerHasJoined()) {
            gameServiceRESTAdapter.sendPutRequestToLetPlayerJoinGame(activeGame.getGameId(), player.getPlayerId());
        }
        player.setGameId(activeGame.getGameId());
        playerRepository.save(player);
        logger.info("Player successfully joined game ");
        return true;
    }


    /**
     * Poll in regular intervals if there is now game open, and if so, join it.
     */
    public void pollForOpenGame() {
        logger.info("Polling for open game ...");
        while (!letPlayerJoinOpenGame()) {
            logger.info("No open game at the moment - polling for open game again in 5 seconds ...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error("pollForOpenGame: sleep interrupted!");
            }
        }
    }

    public void cleanupAfterFinishingGame() {
        logger.info("Cleaning up player ...");
        Player player = queryAndIfNeededCreatePlayer();
        gameApplicationService.finishGame();
        player.setGameId(null);
        playerRepository.save(player);
        logger.info("Cleaned up after finishing game.");
    }

    @Async
    @EventListener(BankInitializedEvent.class)
    public void bankInitialized( BankInitializedEvent event ) {
        logger.info("Bank initialized with {} money.", event.getBalance());
        Player player = queryAndIfNeededCreatePlayer();
        player.initBank(event.getBalance());
        playerRepository.save(player);
    }

    @Async
    @EventListener(BankAccountTransactionBookedEvent.class)
    public void updateBankAccount( BankAccountTransactionBookedEvent event ) {
        Player player = queryAndIfNeededCreatePlayer();
        Integer transaction = event.getTransactionAmount();

        if (transaction > 0)
            player.depositInBank(Money.from(transaction));
        else
            player.withdrawFromBank(Money.from(transaction * -1));

        playerRepository.save(player);
        logger.info("Bank account updated to {} money.", event.getBalance());
        logger.info("Upgrade Budget: {}", player.getUpgradeBudget());
        logger.info("New Robots Budget: {}", player.getNewRobotsBudget());
        logger.info("New Misc Budget: {}", player.getMiscBudget());
    }

    @Async
    @EventListener(RoundStatusEvent.class)
    public void updateRoundStatus( RoundStatusEvent event ) {
        if (!event.getRoundStatus().equals(RoundStatusType.STARTED)) return;

        Player player = queryAndIfNeededCreatePlayer();
        int count = player.getNewRobotsBudget().canBuyThatManyFor(Money.from(100));
        if (count > 0) {
            Command command = Command.createRobotPurchase(count, event.getGameId(), player.getPlayerId());
            gameServiceRESTAdapter.sendPostRequestForCommand(command);
            player.setNewRobotsBudget(player.getNewRobotsBudget().decreaseBy(Money.from(100 * count)));
            logger.info("Bought {} robots", count);
        }
        //TODO: only get your own robots instead of all
        Iterable<Robot> robots = robotRepository.findAll();
        Integer robotCount = 0;
        for (Robot robot : robots) {
            robotCount++;
            Money budget = player.getUpgradeBudget();
            if (robot.canBuyUpgrade(budget)) {
                Capability upgrade = robot.buyUpgrade();
                Command command = Command.createUpgrade(upgrade, robot.getRobotId(), player.getGameId(), player.getPlayerId());
                player.setUpgradeBudget(budget.decreaseBy(upgrade.getUpgradePrice()));
                gameServiceRESTAdapter.sendPostRequestForCommand(command);
            } else {
                if (!robot.hasCommand()) {
                    logger.info("chose command for {}", robot.getRobotId());
                    robot.chooseNextCommand();
                }
                if (robot.hasCommand())
                    gameServiceRESTAdapter.sendPostRequestForCommand(robot.getNextCommand());
                else logger.info("{} is idle", robot.getRobotId());
            }
        }

        robotRepository.saveAll(robots);
        playerRepository.save(player);
        logger.info("Robot Count: {}", robotCount);
        logger.info("Commands send!");
    }
}
