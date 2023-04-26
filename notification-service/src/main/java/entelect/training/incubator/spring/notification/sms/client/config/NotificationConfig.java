package entelect.training.incubator.spring.notification.sms.client.config;

import entelect.training.incubator.spring.notification.sms.client.NotificationServiceConsumer;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sound.midi.Receiver;

@Configuration
class NotificationConfig {
    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange("bookings-event-exchange");
    }

    @Bean
    public Queue queue() {
        return new Queue("bookingServiceQueue");
    }

    @Bean
    public Binding binding (Queue queue, TopicExchange eventExchange) {
        return BindingBuilder
                .bind(queue)
                .to(eventExchange)
                .with("booking.*");
    }

    @Bean
    public NotificationServiceConsumer eventReceiver() {
        return new NotificationServiceConsumer();
    }
}
