package entelect.training.incubator.spring.booking.comms.notifications.impl;

import entelect.training.incubator.spring.booking.comms.bookings.BookingCreatedEvent;
import entelect.training.incubator.spring.booking.comms.bookings.BookingEventsHandler;
import entelect.training.incubator.spring.booking.comms.notifications.NotificationDetails;
import entelect.training.incubator.spring.booking.model.BookingCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationDetailsCommunicator implements NotificationDetails, BookingEventsHandler {
  private final TopicExchange exchange;
  private final RabbitTemplate rabbitTemplate;

  @Value("${notification-service.comms.routing-key}")
  private String routingKey;

  public NotificationDetailsCommunicator(TopicExchange exchange,
                                         RabbitTemplate rabbitTemplate) {
    this.exchange = exchange;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void publishBookingDetails(String flightNumber, String firstName,
                                      String lastName,
                                      String phoneNumber,
                                      String departureTime) {
    final String message =
        "Molo Air: Confirming flight " + flightNumber + " booked for "
            + firstName + " " + lastName + " on " + departureTime;
    final var notification =
        new BookingCreatedMessage(phoneNumber, message);

    rabbitTemplate.convertAndSend(exchange.getName(), routingKey,
        notification);
    log.info("rabbitmq messaging completed");
  }

  @EventListener()
  @Async
  public void handleBookingCreatedEvent(BookingCreatedEvent event) {
    log.info("NotificationsListener: Received booking created event");
    publishBookingDetails(event.getFlightNumber(), event.getFirstName(), event.getLastName(),
            event.getPhoneNumber(), event.getDepartureTime().toString());
  }

}