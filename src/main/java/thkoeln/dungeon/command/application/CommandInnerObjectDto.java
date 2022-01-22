package thkoeln.dungeon.command.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

/**
 * This is the "inner body" as part of a CommandDto when sent to POST gameService/commands.
 * This DTO class is command-specific and provides the appropriate information needed by
 * the consuming service for the command in question. See concrete implementations
 * (per command type) for details.
 * Hint: Please use CommandFactory to obtain this!
 */

// TODO - this is work in progress, not much use for your own player as of now.

@Setter
@Getter
@NoArgsConstructor
@ToString
public abstract class CommandInnerObjectDto {
}
