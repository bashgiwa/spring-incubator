package entelect.training.incubator.spring.booking.exceptions;

public class CustomParameterConstraintException extends RuntimeException {
  public CustomParameterConstraintException() {
    super();
  }

  public CustomParameterConstraintException(final String message) {
    super(message);
  }
}
