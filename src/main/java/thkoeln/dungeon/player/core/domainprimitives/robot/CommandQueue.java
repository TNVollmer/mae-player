package thkoeln.dungeon.player.core.domainprimitives.robot;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import lombok.EqualsAndHashCode;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.command.CommandType;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@EqualsAndHashCode
public class CommandQueue {

    @ElementCollection(targetClass = Command.class, fetch = FetchType.EAGER)
    private final List<Command> queue;

    protected CommandQueue() {
        this.queue = new ArrayList<>();
    }

    protected CommandQueue(List<Command> queue) {
        this.queue = new ArrayList<>(queue);
    }

    public static CommandQueue emptyQueue() {
        return new CommandQueue();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public Integer getSize() {
        return queue.size();
    }

    public CommandType getNextType() {
        return !isEmpty() ? queue.get(0).getCommandType() : null;
    }

    public Command getCommand() {
        return !isEmpty() ? queue.get(0) : null;
    }

    public CommandQueue getPolledQueue() {
        queue.remove(0);
        return new CommandQueue(queue);
    }

    public CommandQueue queueCommand(Command command) {
        List<Command> newQueue = new ArrayList<>(queue);
        newQueue.add(command);
        return new CommandQueue(newQueue);
    }

    public CommandQueue queueAsFirstCommand(Command command) {
        List<Command> newQueue = new ArrayList<>();
        newQueue.add(command);
        newQueue.addAll(queue);
        return new CommandQueue(newQueue);
    }

}
