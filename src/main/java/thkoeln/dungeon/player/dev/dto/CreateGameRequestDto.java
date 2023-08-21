package thkoeln.dungeon.player.dev.dto;

import java.util.UUID;

public record CreateGameRequestDto(int maxRounds, int maxPlayers) {

  public CreateGameRequestDto {
    if(maxRounds < 1) throw new IllegalArgumentException("maxRounds cannot be smaller than 1");
    if(maxPlayers < 1) throw new IllegalArgumentException("maxPlayers cannot be smaller than 1");
  }
}
