package thkoeln.dungeon.monte.core.statusclient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Adapted from Baeldung, https://www.baeldung.com/websockets-spring and
 * https://www.baeldung.com/spring-boot-scheduled-websocket. The Baeldung code is at
 * https://github.com/eugenp/tutorials/tree/master/spring-websockets.
 */

@Getter
@Setter
@AllArgsConstructor
public class OutputMessage {
    private String text;
}
