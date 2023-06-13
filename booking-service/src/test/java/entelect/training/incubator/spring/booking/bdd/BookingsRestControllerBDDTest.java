package entelect.training.incubator.spring.booking.bdd;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "json:target/cucumber-reports/cucumber.json"},
        features = {"src/test/resources/features/CreateBooking"},
        glue = {"steps"})

public class BookingsRestControllerBDDTest {
}
