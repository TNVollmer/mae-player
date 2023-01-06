package thkoeln.dungeon.monte.robot.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.monte.planet.domain.Planet;

import java.util.List;
import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {
    public List<Robot> findAllByAliveEquals( boolean alive );
    public long count();
    public long countAllByAliveIs( boolean alive );
    public long countAllByTypeIs( RobotType type );
    public List<Robot> findAllByLocationIsAndAliveIsTrue( Planet planetRobotIsLocatedOn );
}
