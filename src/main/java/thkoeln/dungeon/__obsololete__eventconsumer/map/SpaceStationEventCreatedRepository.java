package thkoeln.dungeon.__obsololete__eventconsumer.map;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SpaceStationEventCreatedRepository extends CrudRepository<SpaceStationCreatedEvent, UUID> {

}
