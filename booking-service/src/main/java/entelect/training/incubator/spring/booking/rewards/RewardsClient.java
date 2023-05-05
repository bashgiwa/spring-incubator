package entelect.training.incubator.spring.booking.rewards;

import entelect.training.incubator.spring.booking.rewards.stub.CaptureRewardsRequest;
import entelect.training.incubator.spring.booking.rewards.stub.CaptureRewardsResponse;
import entelect.training.incubator.spring.booking.rewards.stub.RewardsBalanceRequest;
import entelect.training.incubator.spring.booking.rewards.stub.RewardsBalanceResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.math.BigDecimal;

public class RewardsClient extends WebServiceGatewaySupport {
    public CaptureRewardsResponse captureRewards(BigDecimal amount, String passportNumber) {
        CaptureRewardsRequest captureRewardsRequest = new CaptureRewardsRequest();
        captureRewardsRequest.setAmount(amount);
        captureRewardsRequest.setPassportNumber(passportNumber);

        CaptureRewardsResponse captureRewardsResponse = (CaptureRewardsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(captureRewardsRequest);

        return captureRewardsResponse;
    }

    public RewardsBalanceResponse rewardsBalance(String passportNumber) {
        RewardsBalanceRequest rewardsBalanceRequest =  new RewardsBalanceRequest();
        rewardsBalanceRequest.setPassportNumber(passportNumber);

        RewardsBalanceResponse rewardsBalanceResponse = (RewardsBalanceResponse) getWebServiceTemplate()
                .marshalSendAndReceive(rewardsBalanceRequest);

        return rewardsBalanceResponse;
    }
}
