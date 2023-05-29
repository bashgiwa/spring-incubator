package entelect.training.incubator.spring.booking.notification;

public interface NotificationDetails {
    void sendBookingNotification(String flightNumber,
                                 String firstName,
                                 String lastName,
                                 String phoneNumber,
                                 String departureTime);
}
