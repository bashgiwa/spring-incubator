package entelect.training.incubator.spring.booking.exceptions;

import io.jsonwebtoken.JwtException;

public class JwtTokenMissingException extends JwtException {
  public JwtTokenMissingException(String message) {
    super(message);
  }

  public JwtTokenMissingException(String message, Throwable cause) {
    super(message, cause);
  }
}
