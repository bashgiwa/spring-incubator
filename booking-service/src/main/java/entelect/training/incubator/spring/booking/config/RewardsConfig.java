package entelect.training.incubator.spring.booking.config;

import entelect.training.incubator.spring.booking.communicator.rewards.RewardsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;


@Configuration
class RewardsConfig {
  static final String jaxbContextPath =
      "entelect.training.incubator.spring.booking.rewards.stub";

  @Bean
  public Jaxb2Marshaller jaxb2Marshaller() {
    Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
    jaxb2Marshaller.setContextPath(jaxbContextPath);
    return jaxb2Marshaller;
  }

  @Bean
  public RewardsClient rewardsClient(final Jaxb2Marshaller jaxb2Marshaller) {
    RewardsClient rewardsClient = new RewardsClient();
    rewardsClient.setDefaultUri("http://localhost:8208/ws");
    rewardsClient.setMarshaller(jaxb2Marshaller);
    rewardsClient.setUnmarshaller(jaxb2Marshaller);
    return rewardsClient;
  }
}
