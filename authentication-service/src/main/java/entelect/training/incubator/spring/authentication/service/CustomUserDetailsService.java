package entelect.training.incubator.spring.authentication.service;

import entelect.training.incubator.spring.authentication.model.User;
import entelect.training.incubator.spring.authentication.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  userRepository.findByUsername(username);
        if(user != null) {
            UserDetails userDetails = (UserDetails) User.builder().username(user.getUsername()).password(user.getPassword());
            return userDetails;
        }else {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }
}
