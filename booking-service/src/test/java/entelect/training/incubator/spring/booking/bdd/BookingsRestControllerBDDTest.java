package entelect.training.incubator.spring.booking.bdd;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features/CreateBooking"},
        glue = {"steps", "config"},
        extraGlue = {"entelect.training.incubator.spring.booking.bdd.config.CucumberSpringConfig"})

public class BookingsRestControllerBDDTest {
}
