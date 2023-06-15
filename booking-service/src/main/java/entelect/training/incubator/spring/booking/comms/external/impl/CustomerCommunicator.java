package entelect.training.incubator.spring.booking.comms.external.impl;


import entelect.training.incubator.spring.booking.comms.external.ExternalCommunicator;
import entelect.training.incubator.spring.booking.exceptions.CustomDataNotFoundException;
import entelect.training.incubator.spring.booking.model.response.CustomerSubscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@CacheConfig(cacheNames = {"customers"})
public class CustomerCommunicator implements ExternalCommunicator {

  @Value("${customer-service.auth.username}")
  private String username;

  @Value("${customer-service.auth.password}")
  private String password;
  @Autowired
  private WebClient webClient;

  @Cacheable(key = "#id")
  @Override
  public Mono<CustomerSubscription> getDetailsById(final String id) {
    log.info(
        "Initiating call to customer service: attempt to retrieve customer with id  " +
            id + " from customer service");

    Mono<CustomerSubscription> customer = webClient
        .get()
        .uri(String.join("", "http://localhost:8201/customers/", id))
        .headers(headers -> headers.setBasicAuth(username, password))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
                error -> Mono.error(new CustomDataNotFoundException( "Unable to retrieve details for customer with id: " + id)))
        .onStatus(HttpStatusCode::is5xxServerError,
                error -> Mono.error(new RuntimeException("Server not responding")))
        .bodyToMono(CustomerSubscription.class)
                .doFinally(t -> log.info("SUCCESSFUL : Customer with id:" + id + " found"));

    return customer;
  }
}
