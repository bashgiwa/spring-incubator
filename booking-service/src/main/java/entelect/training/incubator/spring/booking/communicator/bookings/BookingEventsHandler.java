package entelect.training.incubator.spring.booking.communicator.bookings;

public interface BookingEventsHandler {
    void handleBookingCreatedEvent(BookingCreatedEvent event);
}
