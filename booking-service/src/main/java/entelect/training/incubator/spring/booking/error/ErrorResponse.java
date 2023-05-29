package entelect.training.incubator.spring.booking.error;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
class ErrorResponse {
    private Date timestamp;
    private int errorCode;
    private String status;
    private String message;
}
