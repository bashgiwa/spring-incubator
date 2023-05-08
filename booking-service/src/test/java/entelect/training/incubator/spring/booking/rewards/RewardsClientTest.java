package entelect.training.incubator.spring.booking.rewards;

import entelect.training.incubator.spring.booking.rewards.stub.CaptureRewardsResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RewardsClientTest {
    @Autowired
    RewardsClient client;

    @Test
    public void givenANewBooking_GetRewardsAmount() {
        //CaptureRewardsResponse response = client.captureRewards(amount, passportNumber);
    }
}
