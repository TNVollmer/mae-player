package thkoeln.dungeon.monte.printer.devices.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Adapted from Baeldung, https://www.baeldung.com/websockets-spring and
 * https://www.baeldung.com/spring-boot-scheduled-websocket. The Baeldung code is at
 * https://github.com/eugenp/tutorials/tree/master/spring-websockets.
 */


@Controller
public class StatusController {

    @MessageMapping("/playerstatus")
    @SendTo("/topic/pushstatus")
    public OutputMessage send(String text ) throws Exception {

        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage( text );
    }

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

}
