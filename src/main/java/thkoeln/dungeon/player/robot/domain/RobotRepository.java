package thkoeln.dungeon.player.robot.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {
    Optional<Robot> findByRobotId(UUID id);
    List<Robot> findByRobotType(RobotType type);
}
