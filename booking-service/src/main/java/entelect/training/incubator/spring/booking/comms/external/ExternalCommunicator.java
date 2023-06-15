package entelect.training.incubator.spring.booking.comms.external;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;

public interface ExternalCommunicator {
  Mono<?> getDetailsById(String id);
}
