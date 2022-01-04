package thkoeln.dungeon.eventconsumer.game;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GameStatusEventRepository extends CrudRepository<GameStatusEvent, UUID> {

}
