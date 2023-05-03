package entelect.training.incubator.spring.booking.service;

import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Component;


@Component
public class BookingReferenceGenerator {
    public String generate() {
        RandomString generator = new RandomString(6);
        return generator.nextString();
    }
}
