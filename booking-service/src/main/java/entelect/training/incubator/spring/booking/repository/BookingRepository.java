package entelect.training.incubator.spring.booking.repository;

import entelect.training.incubator.spring.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findBookingByReferenceNumber(@Param("referenceNumber") String referenceNumber);

    List<Booking> findBookingsByCustomerId(@Param("customerId") Integer customerId);
}
