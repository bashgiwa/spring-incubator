package entelect.training.incubator.spring.authentication;

import entelect.training.incubator.spring.authentication.model.User;

import java.util.Map;

public interface JwtGeneratorInterface {
    Map<String, String> generateToken(User user);
}
