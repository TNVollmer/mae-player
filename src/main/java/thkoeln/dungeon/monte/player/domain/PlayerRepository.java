package thkoeln.dungeon.monte.player.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends CrudRepository<Player, UUID> {
    public List<Player> findAll();
    public List<Player> findByPlayerId( UUID playerId );
    public List<Player> findByEnemyShortName( String shortName );
    public boolean existsByEnemyShortName( String shortName );
    public int countAllByEnemyCharIsNotNull();
}
