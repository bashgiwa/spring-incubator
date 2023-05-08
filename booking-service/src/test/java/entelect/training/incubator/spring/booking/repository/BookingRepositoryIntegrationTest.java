package entelect.training.incubator.spring.booking.repository;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.service.BookingReferenceGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class BookingRepositoryIntegrationTest {
    private static final Integer TEST_CUSTOMER_ID = 1;
    private static final Integer TEST_FLIGHT_ID =  1;

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private BookingRepository repository;

    @Test
    public void whenFindByReferenceNumber_thenReturnBooking() {
        Booking booking = createTestBooking(1);
        entityManager.merge(booking);

        assertThat(repository.findBookingByReferenceNumber(booking.getReferenceNumber()))
                .filteredOn("customerId", TEST_CUSTOMER_ID).hasSize(1);
    }

    @Test
    public void whenFindByCustomerId_thenReturnBookings() {
        for(int i=1; i<=3; i++) {
            Booking booking = createTestBooking(i);
            entityManager.merge(booking);
        }
        assertThat(repository.findBookingsByCustomerId(TEST_CUSTOMER_ID))
                .filteredOn("flightId", TEST_FLIGHT_ID)
                .hasSize(3);
    }

    private Booking createTestBooking(Integer id){
        Booking booking =  new Booking();
        BookingReferenceGenerator referenceGenerator = new BookingReferenceGenerator();
        booking.setId(id);
        booking.setCustomerId(TEST_CUSTOMER_ID);
        booking.setFlightId(TEST_FLIGHT_ID);
        booking.setReferenceNumber(referenceGenerator.generate());
        return booking;
    }

}
