package thkoeln.dungeon.command;

import java.util.UUID;

public interface CommandExecutor {
    public UUID executeCommand(Command command );
}
