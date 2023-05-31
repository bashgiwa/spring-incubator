package entelect.training.incubator.spring.booking.communicator.external.impl;


import entelect.training.incubator.spring.booking.communicator.external.ExternalCommunicator;
import entelect.training.incubator.spring.booking.exceptions.CustomDataNotFoundException;
import entelect.training.incubator.spring.booking.model.response.CustomerSubscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class CustomerCommunicator implements ExternalCommunicator {

  @Value("${customer-service.auth.username}")
  private String username;

  @Value("${customer-service.auth.password}")
  private String password;
  @Autowired
  private WebClient webClient;

  @Override
  public ResponseEntity<?> getDetailsById(final String id) {
    log.info(
        "Initiating call to customer service: attempt to retrieve customer with id  " +
            id);

    ResponseEntity<CustomerSubscription> customer = webClient
        .get()
        .uri(String.join("", "http://localhost:8201/customers/", id))
        .headers(headers -> headers.setBasicAuth(username, password))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, error -> Mono.empty())
        .onStatus(HttpStatusCode::is5xxServerError, error -> Mono.empty())
        .toEntity(CustomerSubscription.class).block();

    if (customer.getStatusCode() == HttpStatus.NOT_FOUND) {
      log.error("Customer with id:" + id + " not found");
      throw new CustomDataNotFoundException(
          "Customer with id:" + id + " not found");
    }

    log.info("SUCCESSFUL : Customer with id:" + id + " found");
    return customer;
  }
}
