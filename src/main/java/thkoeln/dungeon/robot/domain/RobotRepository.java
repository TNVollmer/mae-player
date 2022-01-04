package thkoeln.dungeon.robot.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {

}
