package thkoeln.dungeon.core;



import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

/**
 * This class is used to provide samples for event payloads used for testing.
 * The JSON construction uses the event documentation as input.
 */
public class EventPayloadTestFactory {

    public static String timestamp() {
        return ISO_INSTANT.format( ZonedDateTime.now() );
    }


    public static String gameworldCreatedPayload( List<UUID> planetIds ) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("id", UUID.randomUUID().toString() );
        JSONArray spacestation_ids = new JSONArray();
        for ( UUID planetId: planetIds ) {
            spacestation_ids.put( planetId.toString() );
        }
        payload.put("spacestation_ids", spacestation_ids );
        payload.put("status", "active" );

        return payload.toString();
    }

    public static String spaceStationCreatedPayload( UUID planetId ) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("planet_id", planetId );
        return payload.toString();
    }
}
