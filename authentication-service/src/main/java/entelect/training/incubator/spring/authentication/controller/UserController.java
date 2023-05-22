package entelect.training.incubator.spring.authentication.controller;

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


    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try{
            if(request.getUsername() == null || request.getPassword() == null){
                return ResponseEntity.notFound().build();
            }
            AuthResponse response =  userService.login(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
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
