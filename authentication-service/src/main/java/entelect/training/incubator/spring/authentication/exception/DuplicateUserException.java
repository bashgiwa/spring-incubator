package entelect.training.incubator.spring.authentication.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateUserException extends DuplicateKeyException {
  public DuplicateUserException(String msg) {
    super(msg);
  }

  public DuplicateUserException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
