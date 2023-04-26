package entelect.training.incubator.spring.notification.sms.client;

import entelect.training.incubator.spring.notification.sms.client.impl.MoloCellSmsClient;
import entelect.training.incubator.spring.notification.sms.client.model.BookingQueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NotificationServiceConsumer {
  private Logger logger = LoggerFactory.getLogger(NotificationServiceConsumer.class);

  private SmsClient smsClient;

  @Autowired
  NotificationServiceConsumer notificationServiceConsumer(SmsClient smsClient) {
    this.smsClient = smsClient;
    return new NotificationServiceConsumer();
  };

  @RabbitListener(queues = "bookingServiceQueue")
    public void receive(final BookingQueueMessage bookingQueueMessage) {
    logger.info("Recieved message from booking service" + bookingQueueMessage.phoneNumber());
    smsClient.sendSms(bookingQueueMessage.phoneNumber(), bookingQueueMessage.message());
  }
}
