package entelect.training.incubator.spring.booking.communicator.notifications;

public interface NotificationDetails {
    void publishBookingDetails(String flightNumber,
                                 String firstName,
                                 String lastName,
                                 String phoneNumber,
                                 String departureTime);
}
