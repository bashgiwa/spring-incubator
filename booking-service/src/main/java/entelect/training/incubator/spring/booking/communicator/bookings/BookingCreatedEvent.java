package entelect.training.incubator.spring.booking.communicator.bookings;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

public class BookingCreatedEvent {
    private String bookingReference;
    private String flightNumber;
    private String passportNumber;
    private String firstName;
    private String lastName;
    private  String phoneNumber;
    private LocalDateTime departureTime;
    private Float seatCost;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

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

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public Float getSeatCost() {
        return seatCost;
    }

    public void setSeatCost(Float seatCost) {
        this.seatCost = seatCost;
    }
}
