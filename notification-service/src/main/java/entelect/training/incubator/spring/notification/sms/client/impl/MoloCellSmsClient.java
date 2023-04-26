package entelect.training.incubator.spring.notification.sms.client.impl;

import entelect.training.incubator.spring.notification.sms.client.NotificationServiceConsumer;
import entelect.training.incubator.spring.notification.sms.client.SmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * A custom implementation of a fictional SMS service.
 */
@Service
public class MoloCellSmsClient implements SmsClient {
    @Override
    public void sendSms(String phoneNumber, String message) {
        System.out.println(String.format("Sending SMS, destination=%1$s, %2$s", phoneNumber, message));
    }
}
