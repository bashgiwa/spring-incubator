package entelect.training.incubator.spring.notification.sms.client.model;

public record BookingQueueMessage(String phoneNumber,
                                  String message) {
}
