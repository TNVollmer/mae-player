package thkoeln.dungeon.game.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, UUID> {
    public List<Game> findAllByGameStatusEquals( GameStatus gameStatus );
    public List<Game> findAllByGameStatusBetween( GameStatus gameStatus1, GameStatus gameStatus2 );
}
