package entelect.training.incubator.spring.authentication;

import entelect.training.incubator.spring.authentication.model.User;

import java.time.LocalDateTime;

public interface JwtGeneratorInterface {
    String generateToken(User user);

    LocalDateTime getTokenExpiryDate();
}
