package thkoeln.dungeon.monte.game.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, UUID> {
    List<Game> findAllByGameStatusBetween( GameStatus gameStatus1, GameStatus gameStatus2 );
    List<Game> findAll();
}
