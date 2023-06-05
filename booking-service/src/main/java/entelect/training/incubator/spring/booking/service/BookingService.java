package entelect.training.incubator.spring.booking.service;

import entelect.training.incubator.spring.booking.communicator.external.impl.CustomerCommunicator;
import entelect.training.incubator.spring.booking.communicator.external.impl.FlightCommunicator;
import entelect.training.incubator.spring.booking.communicator.notifications.impl.NotificationDetailsCommunicator;
import entelect.training.incubator.spring.booking.communicator.rewards.RewardsClient;
import entelect.training.incubator.spring.booking.communicator.rewards.impl.RewardsClientCommunicator;
import entelect.training.incubator.spring.booking.exceptions.CustomParameterConstraintException;
import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.SearchType;
import entelect.training.incubator.spring.booking.model.request.BookingSearchRequest;
import entelect.training.incubator.spring.booking.model.response.CustomerSubscription;
import entelect.training.incubator.spring.booking.model.response.FlightSubscription;
import entelect.training.incubator.spring.booking.repository.BookingRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
@CacheConfig(cacheNames = {"bookings"})
public class BookingService {

  private final BookingRepository bookingRepository;
  private final BookingReferenceGenerator referenceGenerator;

  @Autowired
  private NotificationDetailsCommunicator notificationComms;
  @Autowired
  private FlightCommunicator flightComms;
  @Autowired
  private CustomerCommunicator customerComms;
  @Autowired
  private RewardsClientCommunicator rewardsComms;

  @Autowired
  BookingService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
    this.referenceGenerator = new BookingReferenceGenerator();
  }

  public Booking createBooking(final Booking booking) {
    booking.setReferenceNumber(referenceGenerator.generate());
    return bookingRepository.save(booking);
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

  public void sendBookingNotification(CustomerSubscription customer,
                                      FlightSubscription flight) {
    notificationComms.sendBookingNotification(flight.getFlightNumber(),
        customer.getFirstName(), customer.getLastName(),
        flight.getFlightNumber(), String.valueOf(flight.getDepartureTime()));
  }

  public ResponseEntity<CustomerSubscription> getCustomerDetailsById(final String id) {
    return customerComms.getDetailsById(id);
  }

  public ResponseEntity<FlightSubscription> getFlightDetailsById(final String id) {
    return flightComms.getDetailsById(id);
  }

  public void onBookingCreated(final CustomerSubscription customer,
                               final FlightSubscription flight) {
    try {
      sendBookingNotification(customer, flight);
      rewardsComms.sendRewardsInformation(
          BigDecimal.valueOf((double) flight.getSeatCost()),
          customer.getPassportNumber());
    } catch (RuntimeException ex) {
      ex.printStackTrace();
    }
    log.info("Booking created successfully ");
  }
}
