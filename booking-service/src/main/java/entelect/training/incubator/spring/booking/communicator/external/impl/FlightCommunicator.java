package entelect.training.incubator.spring.booking.communicator.external.impl;

import entelect.training.incubator.spring.booking.communicator.external.ExternalCommunicator;
import entelect.training.incubator.spring.booking.exceptions.CustomDataNotFoundException;
import entelect.training.incubator.spring.booking.model.response.FlightSubscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@CacheConfig(cacheNames = {"flights"})
public class FlightCommunicator implements ExternalCommunicator {

  @Autowired
  private WebClient webClient;


  @Cacheable(key = "#id")
  @Override
  public ResponseEntity<FlightSubscription> getDetailsById(final String id) {
    log.info(
        "Initiating call to flight service: attempt to retrieve flight with id  " +
            id + " from flight service");

    ResponseEntity<FlightSubscription> flight = webClient
        .get()
        .uri(String.join("", "http://localhost:8202/flights/", id))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, error -> Mono.empty())
        .onStatus(HttpStatusCode::is5xxServerError, error -> Mono.empty())
        .toEntity(FlightSubscription.class).block();

    if (flight.getStatusCode() == HttpStatus.NOT_FOUND) {
      log.error("Unable to retrieve details for flight with id: " + id );
      throw new CustomDataNotFoundException(
          "Unable to retrieve details for flight with id: " + id );
    }

    log.info("SUCCESSFUL : Flight with id:" + id + " found");
    return flight;
  }
}
