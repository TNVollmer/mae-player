package thkoeln.dungeon.command;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.command.Command;

import java.util.UUID;

@Component
public class CommandExecutorService implements CommandExecutor {

    @Override
    public UUID executeCommand( Command command ) {
        return UUID.randomUUID();
    }

}
