package entelect.training.incubator.spring.booking.config;

import entelect.training.incubator.spring.booking.communicator.bookings.BookingEventsPublisher;
import entelect.training.incubator.spring.booking.communicator.external.impl.CustomerCommunicator;
import entelect.training.incubator.spring.booking.communicator.external.impl.FlightCommunicator;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@EnableCaching
public class BookingConfig {

  static final String topicExchange = "bookings-event-exchange";

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(topicExchange);
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      final ConnectionFactory connectionFactory) {
    final var rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public FlightCommunicator flightCommunicator() {
    return new FlightCommunicator();
  }

  @Bean
  public CustomerCommunicator customerCommunicator() {
    return new CustomerCommunicator();
  }

  @Bean("SimpleBookingEventsPublisher")
  public BookingEventsPublisher SimpleBookingEventsPublisher(final ApplicationEventPublisher publisher) {
    return new BookingEventsPublisher(publisher);
  }
  
}

