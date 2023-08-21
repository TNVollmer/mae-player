package thkoeln.dungeon.player.dev.dto;

public record SetRoundDurationRequestDto(int duration) {

  public SetRoundDurationRequestDto {
    if(duration < 1) throw new IllegalArgumentException("duration cannot be smaller than 1");
  }
}
