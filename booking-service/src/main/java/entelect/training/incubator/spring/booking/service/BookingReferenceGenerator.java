package entelect.training.incubator.spring.booking.service;

import net.bytebuddy.utility.RandomString;


public class BookingReferenceGenerator {
    public String generateReference() {
        RandomString generator = new RandomString(6);
        return generator.nextString();
    }
}
