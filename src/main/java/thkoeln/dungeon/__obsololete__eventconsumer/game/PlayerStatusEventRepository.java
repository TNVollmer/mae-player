package thkoeln.dungeon.__obsololete__eventconsumer.game;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PlayerStatusEventRepository extends CrudRepository<PlayerStatusEvent, UUID> {

}
