package entelect.training.incubator.spring.booking.service;

import entelect.training.incubator.spring.booking.comms.bookings.BookingEventsPublisher;
import entelect.training.incubator.spring.booking.comms.external.impl.CustomerCommunicator;
import entelect.training.incubator.spring.booking.comms.external.impl.FlightCommunicator;
import entelect.training.incubator.spring.booking.exceptions.CustomParameterConstraintException;
import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.SearchType;
import entelect.training.incubator.spring.booking.model.request.BookingSearchRequest;
import entelect.training.incubator.spring.booking.model.response.CustomerSubscription;
import entelect.training.incubator.spring.booking.model.response.FlightSubscription;
import entelect.training.incubator.spring.booking.repository.BookingRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
@CacheConfig(cacheNames = {"bookings"})
public class BookingService {

  private final BookingRepository bookingRepository;
  private final BookingReferenceGenerator referenceGenerator;
  private final BookingEventsPublisher bookingEventsPublisher;
  private final FlightCommunicator flightComms;
  private final CustomerCommunicator customerComms;

  @Autowired
  BookingService(BookingRepository bookingRepository,
                 BookingEventsPublisher bookingEventsPublisher,
                 FlightCommunicator flightComms,
                 CustomerCommunicator customerComms) {
    this.bookingRepository = bookingRepository;
    this.bookingEventsPublisher = bookingEventsPublisher;
    this.flightComms = flightComms;
    this.customerComms = customerComms;
    this.referenceGenerator = new BookingReferenceGenerator();
  }

  public Mono<Booking> createBooking(final Booking booking) {
    Mono<CustomerSubscription> customerSub =
            getCustomerDetailsById(
                    booking.getCustomerId().toString());
    Mono<FlightSubscription> flightSub =
            getFlightDetailsById(booking.getFlightId().toString());

    return Mono.zip(customerSub, flightSub)
            .zipWith(saveBooking(booking))
            .doOnNext(tuple -> onBookingCreated(tuple.getT2(), tuple.getT1().getT1(), tuple.getT1().getT2()))
            .map(Tuple2::getT2);
  }

  public Mono<Booking> saveBooking(Booking booking) {
    booking.setReferenceNumber(referenceGenerator.generate());
    final Booking savedBooking = bookingRepository.save(booking);
    return Mono.just(savedBooking);
  }

  public List<Booking> getBookings() {
    return bookingRepository.findAll();
  }

  @Cacheable(key = "#id")
  public Optional<Booking> getBooking(final Integer id) {
    if (id == null) {
      throw new CustomParameterConstraintException(
          "Invalid booking id supplied");
    }
    Optional<Booking> bookingOptional = bookingRepository.findById(id);
    if (bookingOptional.isPresent()) {
      log.info("Booking data fetched from db:: " + id);
    }

    return Optional.ofNullable(bookingOptional.orElse(null));
  }

  @Cacheable(value = "bookings")
  public List<Booking> searchBookings(
      final BookingSearchRequest searchRequest) {
    if (searchRequest == null || searchRequest.getSearchType() == null) {
      throw new CustomParameterConstraintException(
          "Invalid search parameters supplied");
    }
    if(searchRequest.getSearchType() == SearchType.CUSTOMER_ID_SEARCH && searchRequest.getCustomerId() == null) {
      throw new CustomParameterConstraintException(
          "Invalid search parameters : No customer id supplied");
    }
    if(searchRequest.getSearchType() == SearchType.REFERENCE_NUMBER_SEARCH && searchRequest.getReferenceNumber() == null) {
      throw new CustomParameterConstraintException(
          "Invalid search parameters : No reference number supplied");
    }

    log.info("Booking search data fetched from db:: " +
        searchRequest.getSearchType());
    Map<SearchType, Supplier<List<Booking>>> searchStrategies = new HashMap<>();

    searchStrategies.put(SearchType.REFERENCE_NUMBER_SEARCH,
        () -> bookingRepository.findBookingByReferenceNumber(
            searchRequest.getReferenceNumber()));
    searchStrategies.put(SearchType.CUSTOMER_ID_SEARCH,
        () -> bookingRepository.findBookingsByCustomerId(
            searchRequest.getCustomerId()));

    return searchStrategies.get(searchRequest.getSearchType()).get();
  }

  @CacheEvict(key = "#id", allEntries = true)
  public void deleteBooking(final Integer id) {
    bookingRepository.deleteById(id);
  }

  public Mono<CustomerSubscription> getCustomerDetailsById(final String id) {
    return customerComms.getDetailsById(id);
  }

  public Mono<FlightSubscription> getFlightDetailsById(final String id) {
    return flightComms.getDetailsById(id);
  }

  public void onBookingCreated(final Booking booking,
                               final CustomerSubscription customer,
                               final FlightSubscription flight) {
    try {
      log.info(String.valueOf(customer));
      log.info(String.valueOf(flight));
      log.info("Publishing booking created event");
      bookingEventsPublisher.publishNewBookingEvent(booking, customer, flight);
      log.info("Booking created successfully ");
    } catch (NullPointerException ex) {
      ex.printStackTrace();
    }

  }
}
