package entelect.training.incubator.spring.booking.rewards;

import entelect.training.incubator.spring.booking.rewards.stub.CaptureRewardsResponse;
import entelect.training.incubator.spring.booking.rewards.stub.RewardsBalanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import java.math.BigDecimal;

@Slf4j
@Service
public class RewardsClientCommunicator implements RewardsDetails{

    @Autowired
    RewardsClient rewardsClient;

    @Override
    public CaptureRewardsResponse getRewards(BigDecimal amount, String passportNumber) {
        CaptureRewardsResponse captureResponse = rewardsClient.captureRewards(amount,
                passportNumber);
        return captureResponse;
    }

    @Override
    public RewardsBalanceResponse getRewardsBalance(String passportNumber) {
        RewardsBalanceResponse balanceResponse = rewardsClient.rewardsBalance(passportNumber);
        return balanceResponse;
    }

    public void sendRewardsInformation(BigDecimal amount, String passportNumber) {

        try {
            log.info("attempt soap handshake with loyalty service");

            CaptureRewardsResponse rewards = getRewards(amount, passportNumber);
            RewardsBalanceResponse rewardsBalance = getRewardsBalance(passportNumber);

            if(rewards != null && rewardsBalance != null) {
                log.info("soap handshake completed " + rewards.getBalance() + rewardsBalance.getBalance());
            }

        }catch (SoapFaultClientException ex) {
            log.error("Unable to complete soap handshake: " + ex.getFaultStringOrReason());
           throw new RuntimeException(ex);
        }

    }
}
