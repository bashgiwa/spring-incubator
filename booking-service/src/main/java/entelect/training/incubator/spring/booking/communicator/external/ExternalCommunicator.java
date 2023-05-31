package entelect.training.incubator.spring.booking.communicator.external;

import org.springframework.http.ResponseEntity;

public interface ExternalCommunicator {
  ResponseEntity<?> getDetailsById(String id);
}
