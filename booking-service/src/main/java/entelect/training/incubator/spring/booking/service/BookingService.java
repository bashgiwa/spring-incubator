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
import entelect.training.incubator.spring.booking.rewards.stub.RewardsBalanceResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.ws.soap.client.SoapFaultClientException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Service
@AllArgsConstructor
@CacheConfig(cacheNames = {"bookings"})
public class BookingService {
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
            log.info("Booking data fetched from db:: " + id);
        }

        return Optional.ofNullable(bookingOptional.orElse(null));
    }

    @Cacheable(value = "bookings")
    public List<Booking> searchBookings(BookingSearchRequest searchRequest) {
        log.info("Booking search data fetched from db:: " + searchRequest.getSearchType());
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
        log.info("rabbitmq messaging completed");
    }
    public ResponseEntity<CustomerSubscription> getCustomerDetailsById(String id) {

        ResponseEntity<CustomerSubscription> customer = webClient
                .get()
                .uri(String.join("","http://localhost:8201/customers/", id))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, error -> {
                    log.error("Customer with id:  " + id + " not found!");
                    return Mono.error(new RuntimeException("Customer with id: " + id + " not found. " + error));
                })
                .onStatus(HttpStatusCode::is5xxServerError, error -> Mono.error(new RuntimeException("Server error")))
                .toEntity(CustomerSubscription.class).block();

        return customer;
    }

    public ResponseEntity<FlightSubscription> getFlightDetailsById(String id) {

        ResponseEntity<FlightSubscription> flight = webClient
                .get()
                .uri(String.join("","http://localhost:8202/flights/", id))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, error -> {
                    log.error("Flight with id  " + id + " not found!");
                    return Mono.error(new RuntimeException("Flight with id : " + id + " not found. " + error));
                })
                .onStatus(HttpStatusCode::is5xxServerError, error -> Mono.error(new RuntimeException("Server error")))
                .toEntity(FlightSubscription.class).block();

        return flight;
    }

    public void sendRewardsInformation(BigDecimal amount, String passportNumber) {
        try {
            log.info("attempt soap handshake with loyalty service");
            CaptureRewardsResponse captureResponse = rewardsClient.captureRewards(amount,
                    passportNumber);

            RewardsBalanceResponse balanceResponse = rewardsClient.rewardsBalance(passportNumber);

            log.info("soap handshake completed " + captureResponse.getBalance() + balanceResponse.getBalance());
        }catch (SoapFaultClientException ex) {
             log.error("Unable to complete soap handshake: " + ex.getFaultStringOrReason());
             ex.printStackTrace();
//            throw new RuntimeException(ex);
        }

    }

    public void onBookingCreated(CustomerSubscription customer, FlightSubscription flight) {
        sendBookingNotification(customer, flight);

        sendRewardsInformation(BigDecimal.valueOf((double) flight.getSeatCost()), customer.getPassportNumber());
    }
}
