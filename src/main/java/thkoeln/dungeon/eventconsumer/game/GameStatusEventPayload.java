package thkoeln.dungeon.eventconsumer.game;

import thkoeln.dungeon.game.domain.GameStatus;

import java.util.UUID;

public record GameStatusEventPayload( UUID gameId, GameStatus gameStatus ) {
}
