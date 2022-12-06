package thkoeln.dungeon.__obsololete__eventconsumer.robot;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface MovementEventRepository extends CrudRepository<MovementEvent, UUID> {
    List<MovementEvent> findByProcessed(Boolean processed );
}
