package thkoeln.dungeon.command;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.command.CommandExecutor;
import thkoeln.dungeon.command.Command;

import java.util.UUID;

@Component
@Profile( "localKafka" )
public class LocalKafkaCommandExecutor implements CommandExecutor {

    @Override
    public UUID executeCommand( Command command ) {
        return UUID.randomUUID();
    }

}
