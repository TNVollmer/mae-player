package thkoeln.dungeon.__obsololete__eventconsumer.robot;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface NeighboursEventRepository extends CrudRepository<NeighboursEvent, UUID> {
    List<NeighboursEvent> findByTransactionId( UUID transactionId );
}
