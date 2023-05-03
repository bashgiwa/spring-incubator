package entelect.training.incubator.spring.booking.service;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.BookingCreatedMessage;
import entelect.training.incubator.spring.booking.model.BookingSearchRequest;
import entelect.training.incubator.spring.booking.model.SearchType;
import entelect.training.incubator.spring.booking.repository.BookingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    @Cacheable(key="#id")
    public Optional<Booking> getBooking(Integer id) {

        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if(bookingOptional.isPresent()) {
            LOGGER.info("Booking data fetched from db:: " + id);
        }

        return Optional.ofNullable(bookingOptional.orElse(null));
    }

    @Cacheable(value="bookings")
    public List<Booking> searchBookings(BookingSearchRequest searchRequest){
        LOGGER.info("Booking search data fetched from db:: " + searchRequest.getSearchType());
        Map<SearchType, Supplier<List<Booking>>> searchStrategies = new HashMap<>();

        searchStrategies.put(SearchType.REFERENCE_NUMBER_SEARCH, () -> bookingRepository.findBookingByReferenceNumber(searchRequest.getReferenceNumber()));
        searchStrategies.put(SearchType.CUSTOMER_ID_SEARCH, () -> bookingRepository.findBookingsByCustomerId(searchRequest.getCustomerId()));

        return searchStrategies.get(searchRequest.getSearchType()).get();
    }

    @CacheEvict(key="#id", allEntries = true)
    public void deleteBooking(Integer id) {
        bookingRepository.deleteById(id);
    }

    public void sendBookingNotification(LinkedHashMap<String, String> customer, LinkedHashMap<String, String> flight) {
        final String message = "Molo Air: Confirming flight "+ flight.get("flightNumber") + " booked for "
                + customer.get("firstName") + " " + customer.get("lastName") + " on "+ flight.get("departureTime");
        final var notification = new BookingCreatedMessage(customer.get("phoneNumber"), message);

        String routingKey = "booking.created";

        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, notification);
    }
}
