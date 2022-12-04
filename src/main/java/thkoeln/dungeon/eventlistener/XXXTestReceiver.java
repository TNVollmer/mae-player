package thkoeln.dungeon.eventlistener;

import com.rabbitmq.client.Delivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class XXXTestReceiver {
    private Logger logger = LoggerFactory.getLogger(XXXTestReceiver.class);

    public void receiveMessage( byte[] message ) {
       logger.info("xxxxxxxxxxxxxxxxx <" + new String(message) + ">");
    }
}