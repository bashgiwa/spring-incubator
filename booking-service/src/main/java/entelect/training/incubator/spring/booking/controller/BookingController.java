package entelect.training.incubator.spring.booking.controller;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.BookingSearchRequest;
import entelect.training.incubator.spring.booking.response.CustomerSubscription;
import entelect.training.incubator.spring.booking.response.FlightSubscription;
import entelect.training.incubator.spring.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @Operation(summary = "Create a new booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New booking created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Booking.class)) }),
            @ApiResponse(responseCode = "404", description = "Unable to create booking, invalid customer or flight id",
                    content = @Content) })
    @PostMapping
    ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        LOGGER.info("Processing booking creation request for ");


        final ResponseEntity<CustomerSubscription> customer = bookingService.getCustomerDetailsById(booking.getCustomerId().toString());
        if(customer.getStatusCode() == HttpStatus.NOT_FOUND){
            LOGGER.trace("Customer with " + booking.getCustomerId() + "not found");
        }

        final ResponseEntity<FlightSubscription> flight = bookingService.getFlightDetailsById(booking.getFlightId().toString());
        if(flight.getStatusCode() == HttpStatus.NOT_FOUND){
            LOGGER.trace("Flight with " + booking.getCustomerId() + "not found");
        }

        final Booking savedBooking = bookingService.createBooking(booking);
        LOGGER.trace("Booking created" + savedBooking);

        bookingService.onBookingCreated(customer.getBody(), flight.getBody());

        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @Operation(summary = "Find booking by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found booking",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Booking.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Book not found",
                content = @Content) })
    @GetMapping("{id}")
    ResponseEntity<?> getBooking(@Parameter(description = "id of booking to get") @PathVariable Integer id) {
        LOGGER.info("Get booking.. ");
        Optional<Booking> booking = bookingService.getBooking(id);

        if(booking.isPresent()) {
            LOGGER.trace("Booking found , id:: " + id);
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }

        LOGGER.trace("Booking does not exist");
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Find booking by customer id, or by reference number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found booking or bookings",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Booking.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Booking or bookings not found",
                content = @Content) })
    @PostMapping("/search")
    ResponseEntity<?> searchBookings(@Parameter(description = "search parameters to find booking or bookings, booking reference number or customer id")
                                     @RequestBody BookingSearchRequest searchRequest) {
        LOGGER.info("Processing booking search request for request {}", searchRequest);

        List<Booking> bookings = bookingService.searchBookings(searchRequest);

        if(!bookings.isEmpty()) {
            LOGGER.trace("Found bookings: {}", bookings);
            return ResponseEntity.ok(bookings);
        }

        LOGGER.trace("No bookings found");
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a booking by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking deleted successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Booking.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking or bookings not found",
                    content = @Content) })
    @DeleteMapping("{id}")
    ResponseEntity<?> deleteBooking(@PathVariable Integer id) {
        LOGGER.info("Get booking.. ");
        Optional<Booking> booking = bookingService.getBooking(id);

        if(booking.isPresent()) {
            LOGGER.trace("Deleting booking data from db:: " + id);
            bookingService.deleteBooking(id);
            return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
        }

        LOGGER.trace("Booking does not exist");
        return ResponseEntity.notFound().build();
    }
}
