package entelect.training.incubator.spring.authentication.service;

import entelect.training.incubator.spring.authentication.JwtGeneratorInterface;
import entelect.training.incubator.spring.authentication.controller.AuthResponse;
import entelect.training.incubator.spring.authentication.controller.LoginRequest;
import entelect.training.incubator.spring.authentication.controller.RegisterRequest;
import entelect.training.incubator.spring.authentication.exception.UserNotFoundException;
import entelect.training.incubator.spring.authentication.model.User;
import entelect.training.incubator.spring.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class UserService {
    @Value("${application.jwttoken.message}")
    private String message;

    private final UserRepository userRepository;
    private final JwtGeneratorInterface jwtGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final AuthenticationManager authManager;

    @Autowired
    public UserService(UserRepository userRepository,
                       JwtGeneratorInterface jwtGen,
                       AuthenticationManager authManager) {
        this.userRepository = userRepository;
        this.jwtGenerator = jwtGen;
        this.authManager = authManager;
    }

    public User saveUser(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole())
                .build();
        return userRepository.save(user);
    }

    public User getUserByNameAndPassword(String name, String password) throws UserNotFoundException {
        User user =  userRepository.findByUsernameAndPassword(name, password);
        if(user == null) {
            throw new UserNotFoundException("Invalid user name or password");
        }
        return user;
    }

    public AuthResponse login(LoginRequest request) throws UserNotFoundException {
        try {
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            Authentication authentication = authManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(request.getUsername());
            String token = jwtGenerator.generateToken(user);
            LocalDateTime expiryDate = jwtGenerator.getTokenExpiryDate();
            return AuthResponse
                    .builder().username(user.getUsername())
                    .token(String.join(" ", "Bearer", token))
                    .role(user.getRole())
                    .message(message)
                    .expiryDate(expiryDate).build();
        }catch(Exception ex) {
            throw new UserNotFoundException("Unable to authenticate user");
        }

    }
}
