package entelect.training.incubator.spring.booking.communicator.rewards.impl;

import entelect.training.incubator.spring.booking.communicator.rewards.RewardsClient;
import entelect.training.incubator.spring.booking.communicator.rewards.RewardsDetails;
import entelect.training.incubator.spring.booking.rewards.stub.CaptureRewardsResponse;
import entelect.training.incubator.spring.booking.rewards.stub.RewardsBalanceResponse;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Slf4j
@Service
public class RewardsClientCommunicator implements RewardsDetails {

  @Autowired
  RewardsClient rewardsClient;

  @Override
  public CaptureRewardsResponse getRewards(final BigDecimal amount,
                                           final String passportNumber) {
    CaptureRewardsResponse captureResponse =
        rewardsClient.captureRewards(amount,
            passportNumber);
    return captureResponse;
  }

  @Override
  public RewardsBalanceResponse getRewardsBalance(final String passportNumber) {
    RewardsBalanceResponse balanceResponse =
        rewardsClient.rewardsBalance(passportNumber);
    return balanceResponse;
  }

  public void sendRewardsInformation(final BigDecimal amount, final String passportNumber) {

    try {
      log.info("attempt soap handshake with loyalty service");

      CaptureRewardsResponse rewards = getRewards(amount, passportNumber);
      RewardsBalanceResponse rewardsBalance = getRewardsBalance(passportNumber);

      if (rewards != null && rewardsBalance != null) {
        log.info("soap handshake completed " + rewards.getBalance() +
            rewardsBalance.getBalance());
      }

    } catch (SoapFaultClientException ex) {
      log.error(
          "Unable to complete soap handshake: " + ex.getFaultStringOrReason());
      throw new RuntimeException(ex);
    }

  }
}
