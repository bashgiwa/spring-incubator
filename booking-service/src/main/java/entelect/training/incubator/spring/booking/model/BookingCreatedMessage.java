package entelect.training.incubator.spring.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record BookingCreatedMessage (@JsonProperty("phoneNumber") String phoneNumber,
                                     @JsonProperty("message") String message) implements Serializable {
}
