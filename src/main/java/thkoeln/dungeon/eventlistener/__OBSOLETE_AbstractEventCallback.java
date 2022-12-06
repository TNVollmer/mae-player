package thkoeln.dungeon.eventlistener;

import com.rabbitmq.client.Delivery;

import java.io.IOException;

/**
 * Abstract super class to derive event handlers from
 * Todo: create a simple convenient method signature for reading event bodies and acting upon them
 */
public abstract class __OBSOLETE_AbstractEventCallback implements __OBSOLETE_EventCallback {

    @Override
    public void handle( String consumerTag, Delivery delivery ) throws IOException {

    }

    public abstract void executeSpecificActionForEvent( AbstractEvent event );
}
