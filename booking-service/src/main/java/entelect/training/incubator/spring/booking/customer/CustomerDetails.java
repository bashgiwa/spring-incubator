package entelect.training.incubator.spring.booking.customer;

import org.springframework.http.ResponseEntity;

public interface CustomerDetails {
    ResponseEntity<?> getCustomerDetailsById(String id);
}
