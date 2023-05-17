package entelect.training.incubator.spring.booking.service;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.BookingCreatedMessage;
import entelect.training.incubator.spring.booking.model.BookingSearchRequest;
import entelect.training.incubator.spring.booking.model.SearchType;
import entelect.training.incubator.spring.booking.repository.BookingRepository;

import entelect.training.incubator.spring.booking.response.CustomerSubscription;
import entelect.training.incubator.spring.booking.response.FlightSubscription;
import entelect.training.incubator.spring.booking.rewards.RewardsClient;
import entelect.training.incubator.spring.booking.rewards.stub.CaptureRewardsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;

@Service
@CacheConfig(cacheNames = {"bookings"})
public class BookingService {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange exchange;
    private final BookingRepository bookingRepository;
    private final BookingReferenceGenerator referenceGenerator;

    @Autowired
    RewardsClient rewardsClient;

    @Autowired
    WebClient webClient;

    @Autowired
    BookingService(RabbitTemplate rabbitTemplate,
            TopicExchange exchange,
            BookingRepository bookingRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.bookingRepository = bookingRepository;
        this.referenceGenerator = new BookingReferenceGenerator();
    }

    public Booking createBooking(Booking booking) {
        booking.setReferenceNumber(referenceGenerator.generate());
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookings() {
        return (List<Booking>) bookingRepository.findAll();
    }

    @Cacheable(key = "#id")
    public Optional<Booking> getBooking(Integer id) {

        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if(bookingOptional.isPresent()) {
            LOGGER.info("Booking data fetched from db:: " + id);
        }

        return Optional.ofNullable(bookingOptional.orElse(null));
    }

    @Cacheable(value = "bookings")
    public List<Booking> searchBookings(BookingSearchRequest searchRequest) {
        LOGGER.info("Booking search data fetched from db:: " + searchRequest.getSearchType());
        Map<SearchType, Supplier<List<Booking>>> searchStrategies = new HashMap<>();

        searchStrategies.put(SearchType.REFERENCE_NUMBER_SEARCH, () -> bookingRepository.findBookingByReferenceNumber(searchRequest.getReferenceNumber()));
        searchStrategies.put(SearchType.CUSTOMER_ID_SEARCH, () -> bookingRepository.findBookingsByCustomerId(searchRequest.getCustomerId()));

        return searchStrategies.get(searchRequest.getSearchType()).get();
    }

    @CacheEvict(key = "#id", allEntries = true)
    public void deleteBooking(Integer id) {
        bookingRepository.deleteById(id);
    }

    public void sendBookingNotification(CustomerSubscription customer, FlightSubscription flight) {
        final String message = "Molo Air: Confirming flight " + flight.getFlightNumber() + " booked for "
                + customer.getFirstName() + " " + customer.getLastName() + " on " + flight.getDepartureTime();
        final var notification = new BookingCreatedMessage(customer.getPhoneNumber(), message);

        String routingKey = "booking.created";

        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, notification);
        LOGGER.info("rabbitmq messaging completed");
    }
    public ResponseEntity<CustomerSubscription> getCustomerDetailsById(String id) {

        ResponseEntity<CustomerSubscription> customer = webClient
                .get()
                .uri(String.join("","http://localhost:8201/customers/", id))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, error -> Mono.error(new RuntimeException("Flight not found")))
                .onStatus(HttpStatusCode::is5xxServerError, error -> Mono.error(new RuntimeException("Server error")))
                .toEntity(CustomerSubscription.class).block();

        return customer;
    }

    public ResponseEntity<FlightSubscription> getFlightDetailsById(String id) {

        ResponseEntity<FlightSubscription> flight = webClient
                .get()
                .uri(String.join("","http://localhost:8202/flights/", id))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, error -> Mono.error(new RuntimeException("Flight not found")))
                .onStatus(HttpStatusCode::is5xxServerError, error -> Mono.error(new RuntimeException("Server error")))
                .toEntity(FlightSubscription.class).block();

        return flight;
    }

    public void doSOAPHandshake(CustomerSubscription customer, FlightSubscription flight) {
        LOGGER.info("attempt soap handshake");
        CaptureRewardsResponse response = rewardsClient.captureRewards(BigDecimal.valueOf((double) flight.getSeatCost()),
                customer.getPassportNumber());

        LOGGER.info("soap handshake completed " + response.getBalance());
    }

    public void onBookingCreated(CustomerSubscription customer, FlightSubscription flight) {
        sendBookingNotification(customer, flight);

        doSOAPHandshake(customer, flight);
    }
}
