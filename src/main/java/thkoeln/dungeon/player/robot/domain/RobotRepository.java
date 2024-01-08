package thkoeln.dungeon.player.robot.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {
    List<Robot> findAll();

    Robot findByRobotId(UUID robotId);

    List<Robot> findByRobotPlanetPlanetId(UUID planetId);

    Robot findByRobotIdAndRobotPlanetPlanetId(UUID robotId, UUID planetId);

    List<Robot> findByPlayerOwned(Boolean playerOwned);

    List<Robot> findByPendingUpgradePriority(int pendingUpgradePriority);

    List<Robot> findByPlayerOwnedAndRobotPlanetPlanetId(Boolean playerOwned, UUID planetId);
}
