package entelect.training.incubator.spring.booking.communicator.rewards;

import entelect.training.incubator.spring.booking.rewards.stub.CaptureRewardsRequest;
import entelect.training.incubator.spring.booking.rewards.stub.CaptureRewardsResponse;
import entelect.training.incubator.spring.booking.rewards.stub.RewardsBalanceRequest;
import entelect.training.incubator.spring.booking.rewards.stub.RewardsBalanceResponse;
import java.math.BigDecimal;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class RewardsClient extends WebServiceGatewaySupport {
  public CaptureRewardsResponse captureRewards(final BigDecimal amount,
                                               final String passportNumber) {
    CaptureRewardsRequest captureRewardsRequest = new CaptureRewardsRequest();
    captureRewardsRequest.setAmount(amount);
    captureRewardsRequest.setPassportNumber(passportNumber);

    CaptureRewardsResponse captureRewardsResponse =
        (CaptureRewardsResponse) getWebServiceTemplate()
            .marshalSendAndReceive(captureRewardsRequest);

    return captureRewardsResponse;
  }

  public RewardsBalanceResponse rewardsBalance(final String passportNumber) {
    RewardsBalanceRequest rewardsBalanceRequest = new RewardsBalanceRequest();
    rewardsBalanceRequest.setPassportNumber(passportNumber);

    RewardsBalanceResponse rewardsBalanceResponse =
        (RewardsBalanceResponse) getWebServiceTemplate()
            .marshalSendAndReceive(rewardsBalanceRequest);

    return rewardsBalanceResponse;
  }
}
