package entelect.training.incubator.spring.notification.sms.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BookingQueueMessage(@JsonProperty("phoneNumber")String phoneNumber,
                                  @JsonProperty("message") String message) {
}
