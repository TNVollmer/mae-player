package thkoeln.dungeon.monte.core;


import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is used to provide samples for event payloads used for testing.
 * The JSON construction uses the event documentation as input.
 */
public class EventPayloadTestFactory {

    public static String timestamp() {
        // todo clarify timestamp format
        return "999999L";
        // return ISO_INSTANT.format( ZonedDateTime.now() );
    }


    public static String gameworldCreatedPayload( List<UUID> planetIds ) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put( "id", UUID.randomUUID().toString() );
        JSONArray spawnPoint_ids = new JSONArray();
        for ( UUID planetId: planetIds ) {
            spawnPoint_ids.put( planetId.toString() );
        }
        payload.put( "spawnPoint_ids", spawnPoint_ids );
        payload.put( "status", "active" );

        return payload.toString();
    }

    public static String spaceStationCreatedPayload( UUID planetId ) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put( "planet_id", planetId );
        return payload.toString();
    }


    public static String roundStatusPayload( UUID gameId, String status ) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put( "gameId", gameId.toString() );
        payload.put( "roundId", UUID.randomUUID().toString() );
        payload.put( "roundNumber", 12 );
        payload.put( "status", status );

        return payload.toString();
    }

    public static String movementPayload( UUID planetId ) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put( "success", Boolean.TRUE );
        payload.put( "message", "movement successful" );
        payload.put( "remainingEnergy", 16 );

        JSONObject planet = new JSONObject();
        planet.put( "planetId", planetId );
        planet.put( "movementDifficulty", 2 );
        planet.put( "planetType", "SPACESTATION" );
        planet.put( "resourceType", "" );
        payload.put( "planet", planet );

        payload.put( "robots", new JSONArray() );
        return payload.toString();
    }

    public static String neighboursPayload( Map<CompassDirection, UUID> neighbourMap ) throws Exception {
        JSONObject payload = new JSONObject();
        JSONArray neighbours = new JSONArray();
        for ( Map.Entry<CompassDirection, UUID> entry : neighbourMap.entrySet() ) {
            JSONObject neighbour = new JSONObject();
            neighbour.put( "planetId", entry.getValue() );
            neighbour.put( "movementDifficulty", 1 );
            neighbour.put( "direction", entry.getKey() );
            neighbours.put( neighbour );
        }
        payload.put( "neighbours", neighbours );
        return payload.toString();
    }
}
