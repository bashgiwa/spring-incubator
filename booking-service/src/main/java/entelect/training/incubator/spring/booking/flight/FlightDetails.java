package entelect.training.incubator.spring.booking.flight;

import org.springframework.http.ResponseEntity;

public interface FlightDetails {
   ResponseEntity<?> getFlightDetailsById(String id);
}
