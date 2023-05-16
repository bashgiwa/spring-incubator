package entelect.training.incubator.spring.authentication.service;

import entelect.training.incubator.spring.authentication.exception.UserNotFoundException;
import entelect.training.incubator.spring.authentication.model.Role;
import entelect.training.incubator.spring.authentication.model.User;
import entelect.training.incubator.spring.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(User user) {
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    public User getUserByNameAndPassword(String name, String password) throws UserNotFoundException {
        User user =  userRepository.findByUsernameAndPassword(name, password);
        if(user == null) {
            throw new UserNotFoundException("Invalid user name or password");
        }
        return user;
    }

}
