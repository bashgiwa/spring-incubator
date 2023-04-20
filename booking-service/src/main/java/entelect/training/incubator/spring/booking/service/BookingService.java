package entelect.training.incubator.spring.booking.service;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookings() {
        return (List<Booking>) bookingRepository.findAll();
    }

//    public Optional<Booking> getBooking(String bookingReference) {
//        Optional<Booking> bookingOptional = bookingRepository.findByReference(bookingReference);
//
//        return Optional.ofNullable(bookingOptional.orElse(null));
//    }

    public Optional<Booking> getBooking(Integer id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);

        return Optional.ofNullable(bookingOptional.orElse(null));
    }
}
