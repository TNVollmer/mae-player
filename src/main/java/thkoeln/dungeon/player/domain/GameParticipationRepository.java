package thkoeln.dungeon.player.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface GameParticipationRepository extends CrudRepository<GameParticipation, UUID> {
}
