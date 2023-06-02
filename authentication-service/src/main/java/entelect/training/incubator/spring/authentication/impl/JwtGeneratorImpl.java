package entelect.training.incubator.spring.authentication.impl;

import entelect.training.incubator.spring.authentication.JwtGeneratorInterface;
import entelect.training.incubator.spring.authentication.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtGeneratorImpl implements JwtGeneratorInterface {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${application.jwttoken.expiry-period}")
  private Long expiryPeriod;

  @Override
  public String generateToken(User user) {
    log.info("new role created for user:: " + user.getRole().name());

    String jwtToken = "";
    jwtToken = Jwts.builder()
        .setSubject(user.getUsername())
        .claim("app-auth", user.getAuthorities())
        .setIssuedAt(new Date())
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();

    return jwtToken;
  }

  public LocalDateTime getTokenExpiryDate() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expiryDate = now.plusHours(expiryPeriod);
    return expiryDate;
  }
}
