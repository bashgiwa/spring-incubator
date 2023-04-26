package entelect.training.incubator.spring.notification.sms.client;

import entelect.training.incubator.spring.notification.sms.client.model.BookingQueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
public class NotificationServiceConsumer {
  private Logger logger = LoggerFactory.getLogger(NotificationServiceConsumer.class);


  @RabbitListener(queues = "bookingServiceQueue")
    public void receive(final BookingQueueMessage message) {
    logger.info("Recieved message from booking service", message.phoneNumber());
  }
}
