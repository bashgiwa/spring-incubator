package entelect.training.incubator.spring.booking.comms.bookings;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.response.CustomerSubscription;
import entelect.training.incubator.spring.booking.model.response.FlightSubscription;

@Component
public class BookingEventsPublisher  {
    private ApplicationEventPublisher publisher;

    public BookingEventsPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishNewBookingEvent(Booking booking,
                                       CustomerSubscription customer,
                                       FlightSubscription flight) {
        BookingCreatedEvent bookingCreatedEvent = new BookingCreatedEvent(
                booking.getReferenceNumber(), flight.getFlightNumber(),
                customer.getPassportNumber(), customer.getFirstName(),
                customer.getLastName(),customer.getPhoneNumber(),
                flight.getDepartureTime(), flight.getSeatCost());
        try {
            publisher.publishEvent(bookingCreatedEvent);
        }catch(NullPointerException ex) {
            throw new RuntimeException(ex.getMessage());
        }

    }

}
