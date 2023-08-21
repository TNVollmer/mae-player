package thkoeln.dungeon.monte.player.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface PlayerRepository extends CrudRepository<Player, UUID> {
    List<Player> findAll();
}
