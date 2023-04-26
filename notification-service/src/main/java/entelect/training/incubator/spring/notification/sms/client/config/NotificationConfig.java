package entelect.training.incubator.spring.notification.sms.client.config;

import entelect.training.incubator.spring.notification.sms.client.NotificationServiceConsumer;
import entelect.training.incubator.spring.notification.sms.client.SmsClient;
import entelect.training.incubator.spring.notification.sms.client.impl.MoloCellSmsClient;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SmsClient smsClient() {
        return new MoloCellSmsClient();
    }
}
