package entelect.training.incubator.spring.booking.communicator.rewards;

import entelect.training.incubator.spring.booking.rewards.stub.CaptureRewardsResponse;
import entelect.training.incubator.spring.booking.rewards.stub.RewardsBalanceResponse;
import java.math.BigDecimal;

public interface RewardsDetails {
  CaptureRewardsResponse getRewards(BigDecimal amount, String passportNumber);

  RewardsBalanceResponse getRewardsBalance(String passportNumber);
}
