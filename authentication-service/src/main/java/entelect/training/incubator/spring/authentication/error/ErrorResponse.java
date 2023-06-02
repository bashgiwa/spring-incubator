package entelect.training.incubator.spring.authentication.error;


import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ErrorResponse {
    private Date timestamp;
    private int errorCode;
    private String status;
    private String message;
}
