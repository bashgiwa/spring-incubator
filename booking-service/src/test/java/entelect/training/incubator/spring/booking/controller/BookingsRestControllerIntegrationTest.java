package entelect.training.incubator.spring.booking.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import entelect.training.incubator.spring.booking.BookingServiceApplication;
import entelect.training.incubator.spring.booking.exceptions.CustomParameterConstraintException;
import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.request.BookingSearchRequest;
import entelect.training.incubator.spring.booking.model.SearchType;
import entelect.training.incubator.spring.booking.repository.BookingRepository;
import entelect.training.incubator.spring.booking.service.BookingService;
import org.checkerframework.checker.fenum.qual.AwtAlphaCompositingRule;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        mvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).content(toJson(booking)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(TEST_CUSTOMER_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flightId").value(TEST_FLIGHT_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.referenceNumber").isNotEmpty())
                .andDo(print());
    }

    @Test
     public void whenInvalidInput_thenReturnBadRequestResponse() throws Exception {
        final String expectedMessage = "Invalid customer or flight id supplied";

        Booking booking = new Booking();
        booking.setCustomerId(null);
        booking.setFlightId(TEST_FLIGHT_ID);

        mvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).content(toJson(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
            .andDo(print());
//        Exception exception = assertThrows(CustomParameterConstraintException.class, () -> {
//
//        });
//
//        String actualMessage = exception.getMessage();
//
//        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenBookings_whenGetBookingById_thenReturnBooking() throws Exception {
        createTestBooking();

        mvc.perform(get("/bookings/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andDo(print());
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
    public void givenBookings_whenGetBookingsByCustomerId_withInvalidSearchParameters_thenReturnBadRequestResponse()
        throws Exception {
        createTestBooking();

        BookingSearchRequest bookingSearchRequest = new BookingSearchRequest();
        bookingSearchRequest.setSearchType(SearchType.CUSTOMER_ID_SEARCH);

        final String expectedMessage = "Invalid search parameters : No customer id supplied";

        mvc.perform(post("/bookings/search").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(bookingSearchRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
            .andDo(print());

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

    @Test
    public void givenBookings_whenGetBookingsByReferenceNo_withInvalidSearchParameters_thenReturnBadRequestResponse()
        throws Exception {
        createTestBooking();

        BookingSearchRequest bookingSearchRequest = new BookingSearchRequest();
        bookingSearchRequest.setSearchType(SearchType.REFERENCE_NUMBER_SEARCH);

        final String expectedMessage = "Invalid search parameters : No reference number supplied";

        mvc.perform(post("/bookings/search").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(bookingSearchRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
            .andDo(print());

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
