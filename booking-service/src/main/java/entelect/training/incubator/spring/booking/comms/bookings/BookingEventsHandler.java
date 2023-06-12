package entelect.training.incubator.spring.booking.comms.bookings;

public interface BookingEventsHandler {
    void handleBookingCreatedEvent(BookingCreatedEvent event);
}
