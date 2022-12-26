package thkoeln.dungeon.robot.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.robot.domain.RobotRepository;

@Service
public class RobotApplicationService {
    private Logger logger = LoggerFactory.getLogger( RobotApplicationService.class );
    private RobotRepository robotRepository;

    @Autowired
    public RobotApplicationService( RobotRepository robotRepository ) {
        this.robotRepository = robotRepository;
    }


}
