package entelect.training.incubator.spring.booking.exceptions;

import io.jsonwebtoken.MalformedJwtException;

public class JwtTokenMalformedException extends MalformedJwtException {
  public JwtTokenMalformedException(String message) {
    super(message);
  }

  public JwtTokenMalformedException(String message, Throwable cause) {
    super(message, cause);
  }
}
