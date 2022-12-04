package thkoeln.dungeon.eventlistener;

import org.springframework.stereotype.Service;
import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventHeaderPropertiesExtractor {
    private final List<String> headerKeys =
            List.of( "eventId", "type", "version", "transactionId", "timestamp", "playerId" );


    public static Map<String, String> extractPropertiesAsString(Delivery deliveredMessage) {
        HashMap<String, String> headerProperties = new HashMap<>();
        BasicProperties properties = deliveredMessage.getProperties();

        byte[] eventIdBytes = valueOrMissing(properties, "eventId");
        byte[] typeBytes = valueOrMissing(properties, "type");
        byte[] versionBytes = valueOrMissing(properties, "version");
        byte[] transactionIdBytes = valueOrMissing(properties, "transactionId");
        byte[] timestampBytes = valueOrMissing(properties, "timestamp");
        byte[] playerIdBytes = valueOrMissing(properties, "playerId");

        String eventId = new String(eventIdBytes, StandardCharsets.UTF_8);
        String type = new String(typeBytes, StandardCharsets.UTF_8);
        String version = new String(versionBytes, StandardCharsets.UTF_8);
        String transactionId = new String(transactionIdBytes, StandardCharsets.UTF_8);
        String timestamp = new String(timestampBytes, StandardCharsets.UTF_8);
        String playerId = new String(playerIdBytes, StandardCharsets.UTF_8);

        headerProperties.put("eventId", eventId);
        headerProperties.put("type", type);
        headerProperties.put("version", version);
        headerProperties.put("transactionId", transactionId);
        headerProperties.put("timestamp", timestamp);
        headerProperties.put("playerId", playerId);

        return headerProperties;
    }

    private static byte[] valueOrMissing(BasicProperties properties, String key) {
        try {
            return properties.getHeaders().get(key) == null ? ("Missing").getBytes(StandardCharsets.UTF_8) : ((byte[]) properties.getHeaders().get(key));
        } catch (ClassCastException e1) {
            try {
                return properties.getHeaders().get(key) == null ? ("Missing").getBytes(StandardCharsets.UTF_8) : ((LongString) properties.getHeaders().get(key)).getBytes();
            } catch (ClassCastException e2) {
                System.out.println("Fuck!");
                e2.printStackTrace();
            }
        }
        return "null".getBytes(StandardCharsets.UTF_8);
    }
}
