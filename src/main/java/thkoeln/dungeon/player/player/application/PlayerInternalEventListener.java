package thkoeln.dungeon.player.player.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.core.events.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.game.application.GameApplicationService;
import thkoeln.dungeon.player.game.domain.GameStatus;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerInternalEventListener {
    private final GameApplicationService gameApplicationService;
    private final PlayerApplicationService playerApplicationService;
    private final PlayerGameAutoStarter playerGameAutoStarter;

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
}
