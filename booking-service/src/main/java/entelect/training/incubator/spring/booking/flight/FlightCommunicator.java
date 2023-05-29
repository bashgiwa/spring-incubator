package entelect.training.incubator.spring.booking.flight;

import entelect.training.incubator.spring.booking.exceptions.CustomDataNotFoundException;
import entelect.training.incubator.spring.booking.response.FlightSubscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class FlightCommunicator implements FlightDetails {

    @Autowired
    WebClient webClient;
    @Override
    public ResponseEntity<?> getFlightDetailsById(String id) {
        log.info("Initiating call to flight service: attempt to retrieve flight with id  " + id );

        ResponseEntity<FlightSubscription> flight = webClient
                .get()
                .uri(String.join("","http://localhost:8202/flights/", id))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, error -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError, error -> Mono.empty())
                .toEntity(FlightSubscription.class).block();

        if(flight.getStatusCode() == HttpStatus.NOT_FOUND){
            log.error("Flight with id:" + id + " not found");
            throw new CustomDataNotFoundException("Flight with id:" + id + " not found");
        }

        log.info("SUCCESSFUL : Flight with id:" + id + " found");
        return flight;
    }
}
