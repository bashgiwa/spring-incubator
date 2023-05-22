package entelect.training.incubator.spring.authentication.impl;

import entelect.training.incubator.spring.authentication.JwtGeneratorInterface;
import entelect.training.incubator.spring.authentication.model.User;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class JwtGeneratorImpl implements JwtGeneratorInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtGeneratorImpl.class);
    @Value("${jwt.secret}")
    private String secret;

    @Value("${application.jwttoken.expiry-period}")
    private Long expiryPeriod;

    @Override
    public String generateToken(User user) {
        LOGGER.info("new role created for user:: " + user.getRole().name());
        String jwtToken = "";
        jwtToken = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("app-auth", user.getAuthorities())
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secret")
                .compact();

        return jwtToken;
    }

    public LocalDateTime getTokenExpiryDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusHours(expiryPeriod);
        return  expiryDate;
    }
}
