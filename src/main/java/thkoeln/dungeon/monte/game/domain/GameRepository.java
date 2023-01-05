package thkoeln.dungeon.monte.game.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, UUID> {
    public List<Game> findByGameId( UUID gameId );
    public boolean existsByGameId( UUID gameId );
    public List<Game> findAllByGameStatusEquals( GameStatus gameStatus );
    public List<Game> findAllByGameStatusBetween( GameStatus gameStatus1, GameStatus gameStatus2 );
    public List<Game> findAll();
    public Optional<Game> findFirstByGameStatusEquals( GameStatus gameStatus );
}
