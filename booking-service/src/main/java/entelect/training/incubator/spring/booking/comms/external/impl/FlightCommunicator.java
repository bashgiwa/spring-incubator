package entelect.training.incubator.spring.booking.comms.external.impl;

import entelect.training.incubator.spring.booking.comms.external.ExternalCommunicator;
import entelect.training.incubator.spring.booking.exceptions.CustomDataNotFoundException;
import entelect.training.incubator.spring.booking.model.response.FlightSubscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@CacheConfig(cacheNames = {"flights"})
public class FlightCommunicator implements ExternalCommunicator {

  @Autowired
  private WebClient webClient;


  @Cacheable(key = "#id")
  @Override
  public Mono<FlightSubscription> getDetailsById(final String id) {
    log.info(
        "Initiating call to flight service: attempt to retrieve flight with id  " +
            id + " from flight service");

    Mono<FlightSubscription> flight = webClient
        .get()
        .uri(String.join("", "http://localhost:8202/flights/", id))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
                error -> Mono.error(new CustomDataNotFoundException( "Unable to retrieve details for flight with id: " + id)))
        .onStatus(HttpStatusCode::is5xxServerError,
                error -> Mono.error(new RuntimeException("Server not responding")))
        .bodyToMono(FlightSubscription.class);

    log.info("SUCCESSFUL : Flight with id:" + id + " found");
    return flight;
  }
}
