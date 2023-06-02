package entelect.training.incubator.spring.booking.error;

import entelect.training.incubator.spring.booking.exceptions.CustomDataNotFoundException;
import entelect.training.incubator.spring.booking.exceptions.CustomParameterConstraintException;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BookingControllerAdvice {
    @ExceptionHandler(CustomDataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleDataNotFoundExceptions(
        final Exception e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(new Date(),
            status.value(),
            status.name(),
            e.getMessage()), status);
    }

    @ExceptionHandler(CustomParameterConstraintException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidParameters(
        final Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorResponse(new Date(),
            status.value(),
            status.name(),
            e.getMessage()), status);
    }
}
