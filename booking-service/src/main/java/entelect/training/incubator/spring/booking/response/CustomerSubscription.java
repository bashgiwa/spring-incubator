package entelect.training.incubator.spring.booking.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerSubscription {
    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String passportNumber;
    private String email;
    private String phoneNumber;
}
