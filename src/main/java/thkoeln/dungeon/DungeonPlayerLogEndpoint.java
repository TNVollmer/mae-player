package thkoeln.dungeon;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import thkoeln.dungeon.player.application.PlayerApplicationService;


/**
 * (c) Tobi, https://github.com/The-Microservice-Dungeon/gamelog/blob/main/src/main/java/com/github/tmd/gamelog/core/LogsEndpoint.java
 */
@Component
@Endpoint(id = "logs")
@Slf4j
public class DungeonPlayerLogEndpoint {
    private final String logFilePath;
    protected Logger logger = LoggerFactory.getLogger(PlayerApplicationService.class);

    @Autowired
    public DungeonPlayerLogEndpoint( @Value("${logging.file.path}") String logFilePath) {
        this.logFilePath = logFilePath;
    }

    /**
     * GET /actuator/logs
     */
    @ReadOperation(produces = MediaType.TEXT_PLAIN_VALUE)
    public String logs() {
        Path path = Paths.get(logFilePath + "/spring.log");

        try {
            Stream<String> lines = Files.lines(path);
            String data = lines.collect(Collectors.joining("\n"));
            lines.close();

            return data;
        } catch (IOException e) {
            logger.error( "Could not read log file", e );
        }
        throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR );
    }
}
