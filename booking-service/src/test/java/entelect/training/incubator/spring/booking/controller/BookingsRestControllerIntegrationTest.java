package entelect.training.incubator.spring.booking.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import entelect.training.incubator.spring.booking.BookingServiceApplication;
import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.BookingSearchRequest;
import entelect.training.incubator.spring.booking.model.SearchType;
import entelect.training.incubator.spring.booking.repository.BookingRepository;
import entelect.training.incubator.spring.booking.service.BookingService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.http.MatcherType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BookingServiceApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingsRestControllerIntegrationTest {
    private static final Integer TEST_CUSTOMER_ID = 1;

    private static final Integer TEST_FLIGHT_ID = 1;

    @Autowired
    private MockMvc mvc;

    @Autowired
    BookingRepository repository;

    @Autowired
    BookingService bookingService;

    @Test
    public void whenValidInput_thenCreateBooking() throws Exception {
        Booking booking = new Booking();
        booking.setCustomerId(TEST_CUSTOMER_ID);
        booking.setFlightId(TEST_FLIGHT_ID);

        mvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).content(toJson(booking)));

        List<Booking> found = (List<Booking>) repository.findAll();
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getCustomerId()).isEqualTo(TEST_CUSTOMER_ID);
    }

    @Test
    public void givenBookings_whenGetBookingById_thenReturnBooking() throws Exception {
        createTestBooking();

        mvc.perform(get("/bookings/1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void givenBookings_whenGetBookingsByCustomerId_thenReturnBookings() throws Exception {
        createTestBooking();

        BookingSearchRequest bookingSearchRequest = new BookingSearchRequest();
        bookingSearchRequest.setSearchType(SearchType.CUSTOMER_ID_SEARCH);
        bookingSearchRequest.setCustomerId(TEST_CUSTOMER_ID);

        mvc.perform(post("/bookings/search").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(bookingSearchRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        List<Booking> found = (List<Booking>) repository.findBookingsByCustomerId(TEST_CUSTOMER_ID);
        assertThat(found).extracting(Booking::getCustomerId).contains(TEST_CUSTOMER_ID);
    }

    @Test
    public void givenBookings_whenGetBookingsByReferenceNumber_thenReturnBooking() throws Exception {
        Booking booking = createTestBooking();
        String TEST_REFERENCE_NUMBER = booking.getReferenceNumber();

        BookingSearchRequest bookingSearchRequest = new BookingSearchRequest();
        bookingSearchRequest.setSearchType(SearchType.REFERENCE_NUMBER_SEARCH);
        bookingSearchRequest.setReferenceNumber(TEST_REFERENCE_NUMBER);

        mvc.perform(post("/bookings/search").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(bookingSearchRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].referenceNumber", is(TEST_REFERENCE_NUMBER)));

    }

    private Booking createTestBooking() {
        Booking booking = new Booking();
        booking.setCustomerId(TEST_CUSTOMER_ID);
        booking.setFlightId(TEST_FLIGHT_ID);
        return bookingService.createBooking(booking);
    }

    private static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
