package thkoeln.dungeon.player.core.domainprimitives.robot;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.domainprimitives.command.CommandType;

import java.util.PriorityQueue;
import java.util.Queue;

@NoArgsConstructor
public class CommandQueue {

    private Queue<Command> queue;

    protected CommandQueue(Queue<Command> queue) {
        this.queue = new PriorityQueue<>(queue);
    }

    public static CommandQueue emptyQueue() {
        return new CommandQueue();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public CommandType getNextType() {
        return queue.peek() != null ? queue.peek().getCommandType() : null;
    }

    public Command getCommand() {
        return queue.peek();
    }

    public CommandQueue getPolledQueue() {
        queue.poll();
        return new CommandQueue(queue);
    }

    public CommandQueue queueCommand(Command command) {
        Queue<Command> newQueue = new PriorityQueue<>(queue);
        newQueue.add(command);
        return new CommandQueue(newQueue);
    }

    public CommandQueue queueAsFirstCommand(Command command) {
        Queue<Command> newQueue = new PriorityQueue<>();
        newQueue.add(command);
        newQueue.addAll(queue);
        return new CommandQueue(newQueue);
    }

}
