package entelect.training.incubator.spring.booking.controller;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.BookingSearchRequest;
import entelect.training.incubator.spring.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("bookings")
public class BookingController {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    @Autowired
    BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        LOGGER.info("Processing booking creation request for ");

        ResponseEntity<Object> customer = new RestTemplate()
                .getForEntity("http://localhost:8201/customers/"+ booking.getCustomerId(), Object.class);

        ResponseEntity<Object> flight = new RestTemplate()
                .getForEntity("http://localhost:8202/flights/"+ booking.getFlightId(), Object.class);

        if(customer.getStatusCode() == HttpStatus.NOT_FOUND ){
            LOGGER.trace("Customer with "+ booking.getCustomerId() + "not found");
            return ResponseEntity.notFound().build();
        }

        if(flight.getStatusCode() == HttpStatus.NOT_FOUND){
            LOGGER.trace("Flight with "+ booking.getFlightId() + "not found");
            return ResponseEntity.notFound().build();
        }

        final Booking savedBooking = bookingService.createBooking(booking);
        bookingService.sendBookingNotification((LinkedHashMap<String, String>) customer.getBody(), (LinkedHashMap<String, String>) flight.getBody());

        LOGGER.trace("Booking created");
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    ResponseEntity<?> getBooking(@PathVariable Integer id) {
        LOGGER.info("Get booking.. ");
        Optional<Booking> booking =  bookingService.getBooking(id);

        if(booking.isPresent()){
            LOGGER.trace("Booking found , id:: " + id);
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }

        LOGGER.trace("Booking does not exist");
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/search")
    ResponseEntity<?> searchBookings(@RequestBody BookingSearchRequest searchRequest) {
        LOGGER.info("Processing booking search request for request {}", searchRequest);

        List<Booking> bookings = bookingService.searchBookings(searchRequest);

        if(!bookings.isEmpty()){
            LOGGER.trace("Found bookings: {}", bookings);
            return ResponseEntity.ok(bookings);
        }

        LOGGER.trace("No bookings found");
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    ResponseEntity<?> deleteBooking(@PathVariable Integer id) {
        LOGGER.info("Get booking.. ");
        Optional<Booking> booking =  bookingService.getBooking(id);

        if(booking.isPresent()){
            LOGGER.trace("Deleting booking data from db:: " + id);
            bookingService.deleteBooking(id);
            return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
        }

        LOGGER.trace("Booking does not exist");
        return ResponseEntity.notFound().build();
    }

}
