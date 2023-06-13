package entelect.training.incubator.spring.booking.bdd.config;

import org.springframework.boot.test.context.SpringBootTest;

import entelect.training.incubator.spring.booking.BookingServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BookingServiceApplication.class)
class CucumberSpringConfig {
}
