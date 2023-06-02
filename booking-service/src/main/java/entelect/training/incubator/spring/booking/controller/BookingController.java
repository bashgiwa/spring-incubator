package entelect.training.incubator.spring.booking.controller;

import entelect.training.incubator.spring.booking.communicator.external.impl.CustomerCommunicator;
import entelect.training.incubator.spring.booking.exceptions.CustomDataNotFoundException;
import entelect.training.incubator.spring.booking.exceptions.CustomParameterConstraintException;
import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.request.BookingSearchRequest;
import entelect.training.incubator.spring.booking.model.response.CustomerSubscription;
import entelect.training.incubator.spring.booking.model.response.FlightSubscription;
import entelect.training.incubator.spring.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("bookings")
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;

  @Operation(summary = "Create a new booking")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "New booking created",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Booking.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid customer or flight id supplied",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Unable to retrieve details for customer or flight with given id",
          content = @Content)})
  @PostMapping
  ResponseEntity<?> createBooking(@RequestBody final Booking booking) {
    log.info("Processing booking creation request for ");

    if (booking.getCustomerId() == null || booking.getFlightId() == null) {
      throw new CustomParameterConstraintException(
          "Invalid customer or flight id supplied");
    }

    ResponseEntity<CustomerSubscription> customer =
        bookingService.getCustomerDetailsById(
            booking.getCustomerId().toString());
    ResponseEntity<FlightSubscription> flight =
        bookingService.getFlightDetailsById(booking.getFlightId().toString());

    final Booking savedBooking = bookingService.createBooking(booking);
    log.trace("Booking created " + savedBooking);

    bookingService.onBookingCreated(customer.getBody(), flight.getBody());

    return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
  }

  @Operation(summary = "Find booking by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found booking",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Booking.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid id supplied",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Book not found",
          content = @Content)})
  @GetMapping("{id}")
  ResponseEntity<?> getBooking(
      @Parameter(description = "id of booking to get") @PathVariable
      final Integer id) {
    if (id == null) {
      throw new CustomParameterConstraintException(
          "Invalid booking id supplied");
    }
    log.info("Get booking.. ");
    Optional<Booking> booking = bookingService.getBooking(id);

    if (booking.isEmpty()) {
      throw new CustomDataNotFoundException(
          "Booking with id " + id + " does not exist");
    }
    log.trace("Booking found , id:: " + id);
    return new ResponseEntity<>(booking, HttpStatus.OK);
  }

  @Operation(summary = "Find booking by customer id, or by reference number")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found booking or bookings",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Booking.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid search parameters supplied",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Booking or bookings not found",
          content = @Content)})
  @PostMapping("/search")
  ResponseEntity<?> searchBookings(
      @Parameter(description = "search parameters to find booking or bookings, booking reference number or customer id")
      @RequestBody final BookingSearchRequest searchRequest) {

    if (searchRequest == null || searchRequest.getSearchType() == null
        || (searchRequest.getReferenceNumber() == null && searchRequest.getCustomerId() == null)) {
      throw new CustomParameterConstraintException(
          "Invalid search parameters supplied");
    }
    log.info("Processing booking search request for request {}", searchRequest);

    List<Booking> bookings = bookingService.searchBookings(searchRequest);

    if (bookings.isEmpty()) {
      log.trace("Booking or bookings not found");
      throw new CustomDataNotFoundException("Booking or bookings not found");
    }

    log.trace("Found bookings: {}", bookings);
    return ResponseEntity.ok(bookings);
  }

  @Operation(summary = "Delete a booking by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Booking deleted successfully",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Booking.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid id supplied",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Booking not found",
          content = @Content)})
  @DeleteMapping("{id}")
  ResponseEntity<?> deleteBooking(@PathVariable final Integer id) {
    log.info("Get booking.. ");
    Optional<Booking> booking = bookingService.getBooking(id);

    if (booking.isEmpty()) {
      log.info("Booking does not exist");
      throw new CustomDataNotFoundException("Booking not found");
    }

    log.trace("Deleting booking data from db:: " + id);
    bookingService.deleteBooking(id);
    return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
  }
}
