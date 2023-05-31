package entelect.training.incubator.spring.booking.model.request;

import entelect.training.incubator.spring.booking.model.SearchType;
import lombok.Data;

@Data
public class BookingSearchRequest {
    private SearchType searchType;
    private String referenceNumber;
    private Integer customerId;
}
