package entelect.training.incubator.spring.booking.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FlightSubscription {
    private Integer id;

    private String flightNumber;

    private String origin;

    private String destination;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private Integer seatsAvailable;

    private Float seatCost;
}
