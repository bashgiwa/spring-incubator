package entelect.training.incubator.spring.authentication.service;

import entelect.training.incubator.spring.authentication.model.User;
import entelect.training.incubator.spring.authentication.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;


  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user != null) {
      UserDetails userDetails = User
          .builder()
          .id(user.getId())
          .username(user.getUsername())
          .password(user.getPassword())
          .email(user.getEmail())
          .role(user.getRole())
          .build();
      return userDetails;
    } else {
      throw new UsernameNotFoundException("Invalid username or password");
    }

  }
}
