package entelect.training.incubator.spring.authentication.error;

import entelect.training.incubator.spring.authentication.exception.CustomParameterConstraintException;
import entelect.training.incubator.spring.authentication.exception.DuplicateUserException;
import entelect.training.incubator.spring.authentication.exception.UserNotFoundException;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserControllerAdvice {
  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  ResponseEntity<ErrorResponse> handleDataNotFoundException(final Exception e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    return new ResponseEntity<>(new ErrorResponse(new Date(),
        status.value(),
        status.name(),
        e.getMessage()), status);
  }

  @ExceptionHandler(DuplicateUserException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  ResponseEntity<ErrorResponse> handleDuplicateUserException(final Exception e) {
    HttpStatus status = HttpStatus.CONFLICT;
    return new ResponseEntity<>(new ErrorResponse(new Date(),
        status.value(),
        status.name(),
        e.getMessage()), status);
  }

  @ExceptionHandler(CustomParameterConstraintException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ResponseEntity<ErrorResponse> handleIncorrectRegistrationDetailsException(final Exception e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return new ResponseEntity<>(new ErrorResponse(new Date(),
        status.value(),
        status.name(),
        e.getMessage()), status);
  }
}
