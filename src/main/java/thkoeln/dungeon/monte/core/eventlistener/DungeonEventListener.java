package thkoeln.dungeon.monte.core.eventlistener;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.core.annotation.AliasFor;
import org.springframework.messaging.handler.annotation.MessageMapping;

/**
 * Meta annotation that takes care of creating RabbitMQ listeners.
 * The annotation behaves the same as {@link org.springframework.amqp.rabbit.annotation.RabbitListener},
 * however it only exposes the binding key for customization.
 *
 * @see org.springframework.amqp.rabbit.annotation.RabbitListener
 */
@RabbitListener( bindings = @QueueBinding(
    exchange = @Exchange(name = "player-${dungeon.playerName}", type = "topic", declare = "false"),
    value = @Queue
))
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
public @interface DungeonEventListener {

  /**
   * The binding key used to bind the queue of the event listener to player exchange
   * @return binding key
   */
  @AliasFor(annotation = RabbitListener.class, attribute = "key")
  String value() default "";
}
