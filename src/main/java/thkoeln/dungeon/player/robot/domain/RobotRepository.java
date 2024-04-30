package thkoeln.dungeon.player.robot.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.player.core.domainprimitives.status.Activity;
import thkoeln.dungeon.player.planet.domain.Planet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {
    List<Robot> findByCurrentActivity(Activity activity);
    Optional<Robot> findByRobotId(UUID id);
    List<Robot> findByPlanet(Planet planet);
}
