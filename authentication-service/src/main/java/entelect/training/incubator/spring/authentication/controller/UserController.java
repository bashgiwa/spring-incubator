package entelect.training.incubator.spring.authentication.controller;

import entelect.training.incubator.spring.authentication.JwtGeneratorInterface;
import entelect.training.incubator.spring.authentication.exception.UserNotFoundException;
import entelect.training.incubator.spring.authentication.model.User;
import entelect.training.incubator.spring.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
class UserController {
    private UserService userService;
    private JwtGeneratorInterface jwtGenerator;

    @Autowired
    UserController(UserService userService, JwtGeneratorInterface jwtGenerator) {
        this.userService = userService;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/login")
    ResponseEntity<?> loginUser(@RequestBody User user) {
        try{
            if(user.getUsername() == null || user.getPassword() == null){
                throw new UserNotFoundException("Username or password is empty");
            }
            User userdata = userService.getUserByNameAndPassword(user.getUsername(), user.getPassword());
            if(userdata == null) {
                throw new UserNotFoundException("Username or password is invalid");
            }
            return new ResponseEntity<>(jwtGenerator.generateToken(userdata), HttpStatus.OK);
        }catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request){
        try {
            final User newUser = userService.saveUser(request);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
 }
