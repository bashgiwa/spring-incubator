package entelect.training.incubator.spring.booking.comms.external;

import org.springframework.http.ResponseEntity;

public interface ExternalCommunicator {
  ResponseEntity<?> getDetailsById(String id);
}
