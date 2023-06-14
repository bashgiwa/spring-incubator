package entelect.training.incubator.spring.booking.bdd.steps;

import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;
import lombok.Data;

@Component
@ScenarioScope
@Data
public class LoginInformation {
    private String bearerToken;
}
