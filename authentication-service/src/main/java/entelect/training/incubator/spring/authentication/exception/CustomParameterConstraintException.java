package entelect.training.incubator.spring.authentication.exception;

public class CustomParameterConstraintException extends RuntimeException {
  public CustomParameterConstraintException() {
    super();
  }

  public CustomParameterConstraintException(final String message) {
    super(message);
  }
}
