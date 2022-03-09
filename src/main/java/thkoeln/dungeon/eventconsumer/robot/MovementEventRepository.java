package thkoeln.dungeon.eventconsumer.robot;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MovementEventRepository extends CrudRepository<MovementEvent, UUID> {

}
