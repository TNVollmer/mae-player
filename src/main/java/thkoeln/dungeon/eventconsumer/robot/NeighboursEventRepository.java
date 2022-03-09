package thkoeln.dungeon.eventconsumer.robot;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface NeighboursEventRepository extends CrudRepository<NeighboursEvent, UUID> {

}
