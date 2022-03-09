package thkoeln.dungeon.eventconsumer.map;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.eventconsumer.game.PlayerStatusEvent;

import java.util.UUID;

public interface GameWorldCreatedEventRepository extends CrudRepository<GameWorldCreatedEvent, UUID> {

}
