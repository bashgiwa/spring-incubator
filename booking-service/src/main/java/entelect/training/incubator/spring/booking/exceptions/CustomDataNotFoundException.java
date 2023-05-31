package entelect.training.incubator.spring.booking.exceptions;

public class CustomDataNotFoundException extends RuntimeException {
    public CustomDataNotFoundException() {
        super();
    }

    public CustomDataNotFoundException(final String message) {
        super(message);
    }
}
