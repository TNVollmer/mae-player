package thkoeln.dungeon.monte.robot.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {
    public List<Robot> findAllByAliveEquals( boolean alive );
    public List<Robot> findAllByEnemyCharIsNullAndAliveEqualsOrderByType( boolean alive );
    public List<Robot> findAllByEnemyCharIsNotNullAndAliveEqualsOrderByEnemyChar( boolean alive );
    public Optional<Robot> findByRobotId( UUID robotId );
    public boolean existsByRobotId( UUID robotId );
    public long count();
    public long countAllByEnemyCharIsNullAndAliveIs( boolean alive );
    public long countAllByTypeIs( RobotType type );
}
