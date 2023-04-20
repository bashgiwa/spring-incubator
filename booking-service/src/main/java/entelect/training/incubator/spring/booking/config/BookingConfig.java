package entelect.training.incubator.spring.booking.config;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.repository.BookingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BookingConfig {
    @Bean
    CommandLineRunner commandLineRunner (BookingRepository repository) {
        return args -> {
            Booking alpha =  new Booking(
                    1234, 45, "beta12");
            Booking beta =  new Booking(
                    4578, 99, "beta24");
            repository.saveAll(List.of(alpha, beta));
        };
    }
}

