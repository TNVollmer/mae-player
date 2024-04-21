package thkoeln.dungeon.player.mock.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.player.core.domainprimitives.command.Command;
import thkoeln.dungeon.player.core.events.AbstractEvent;
import thkoeln.dungeon.player.core.events.EventHeader;
import thkoeln.dungeon.player.core.events.EventType;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TestHelper {
    private static final Logger logger = LoggerFactory.getLogger(TestHelper.class);
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RabbitAdmin rabbitAdmin;

    private final String mockHost;

    @Autowired
    public TestHelper(@Value("${dungeon.mock.host}") String mockHost) {
        this.mockHost = mockHost;
    }

    public void setupEventMap(Map<String, List<String>> forwardedEvents) {
        forwardedEvents.put(EventType.GAME_STATUS.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.ROUND_STATUS.getStringValue(), new ArrayList<>());

        forwardedEvents.put(EventType.PLANET_DISCOVERED.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.RESOURCE_MINED.getStringValue(), new ArrayList<>());

        forwardedEvents.put(EventType.ROBOT_ATTACKED.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.ROBOT_MOVED.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.ROBOT_REGENERATED.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.ROBOT_RESOURCE_MINED.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.ROBOT_RESOURCE_REMOVED.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.ROBOT_RESTORED_ATTRIBUTES.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.ROBOT_SPAWNED.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.ROBOT_REVEALED.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.ROBOT_UPGRADED.getStringValue(), new ArrayList<>());

        forwardedEvents.put(EventType.BANK_ACCOUNT_CLEARED.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.BANK_INITIALIZED.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.BANK_ACCOUNT_TRANSACTION_BOOKED.getStringValue(), new ArrayList<>());

        forwardedEvents.put(EventType.TRADABLE_BOUGHT.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.TRADABLE_PRICES.getStringValue(), new ArrayList<>());
        forwardedEvents.put(EventType.TRADABLE_SOLD.getStringValue(), new ArrayList<>());

        forwardedEvents.put(EventType.ERROR.getStringValue(), new ArrayList<>());
    }

    public void consumeAllMessagesInQueue(String queue, Map<String, List<String>> events) {
        boolean queueStillFull = true;
        while (queueStillFull) {
            Message message = this.rabbitAdmin.getRabbitTemplate().receive(queue);
            if (message != null) {
                String eventType = new String(message.getMessageProperties().getHeader(EventHeader.getTYPE_KEY()), StandardCharsets.UTF_8);
                String eventBody = new String(message.getBody(), StandardCharsets.UTF_8);

                events.get(eventType).add(eventBody);
            } else {
                queueStillFull = false;
            }
        }
    }

    public List<AbstractEvent> consumeAllEventsOfTypeInEventQueue(String queue, Class<? extends AbstractEvent> eventClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<AbstractEvent> events = new ArrayList<>();
        boolean queueStillFull = true;
        while (queueStillFull) {
            AbstractEvent event = this.consumeNextEventOfTypeInEventQueue(queue, eventClass);
            if (event != null) {
                events.add(event);
            } else {
                queueStillFull = false;
            }
        }
        return events;
    }

    public AbstractEvent consumeNextEventOfTypeInEventQueue(String queue, Class<? extends AbstractEvent> eventClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Message message = this.rabbitAdmin.getRabbitTemplate().receive(queue);
        if (message != null) {
            String eventBody = new String(message.getBody(), StandardCharsets.UTF_8);
            AbstractEvent event = eventClass.getDeclaredConstructor().newInstance();

            event.setEventHeader(null);
            event.fillWithPayload(eventBody);

            return event;
        } else {
            return null;
        }
    }

    public void sendCommand(Command command) throws JsonProcessingException {
        String jsonRequest = objectMapper.writeValueAsString(command);
        logger.info("Requested command: " + jsonRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> postResponse = restTemplate.postForEntity(
                mockHost + "/commands",
                new HttpEntity<>(jsonRequest, headers), String.class
        );

        logger.info("Http response: " + postResponse.getBody());
    }

    public void createNewEventQueueWithEventTypeBinding(String newEventQueueName, String playerExchange, EventType eventType) {
        Queue newEventQueue = QueueBuilder
                .durable(newEventQueueName)
                .build();

        Binding newEventTypeBinding = BindingBuilder
                .bind(newEventQueue)
                .to((Exchange) ExchangeBuilder
                        .topicExchange(playerExchange)
                        .build()
                )
                .with("IGNORED-NEW-EVENT-TYPE-BINDING")
                .and(Map.of("x-match", "all",
                        EventHeader.getTYPE_KEY(), eventType.getStringValue())
                );
        this.createNewEventQueueWithBinding(newEventQueue, newEventTypeBinding);
    }

    public void createNewEventQueueWithBinding(Queue eventQueue, Binding binding) {
        this.rabbitAdmin.declareQueue(eventQueue);
        this.rabbitAdmin.declareBinding(binding);

        this.rabbitAdmin.purgeQueue(eventQueue.getName());
    }

}
