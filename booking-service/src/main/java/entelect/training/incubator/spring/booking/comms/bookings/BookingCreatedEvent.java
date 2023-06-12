package entelect.training.incubator.spring.booking.comms.bookings;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookingCreatedEvent {
    private String bookingReference;
    private String flightNumber;
    private String passportNumber;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDateTime departureTime;
    private Float seatCost;

    public BookingCreatedEvent (
                               String bookingReference,
                               String flightNumber,
                               String passportNumber,
                               String firstName,
                               String lastName,
                               String phoneNumber,
                               LocalDateTime departureTime,
                               Float seatCost) {

        this.bookingReference = bookingReference;
        this.flightNumber = flightNumber;
        this.passportNumber = passportNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.departureTime = departureTime;
        this.seatCost = seatCost;
    }
}
