package entelect.training.incubator.spring.authentication.controller;

import entelect.training.incubator.spring.authentication.exception.CustomParameterConstraintException;
import entelect.training.incubator.spring.authentication.exception.DuplicateUserException;
import entelect.training.incubator.spring.authentication.exception.UserNotFoundException;
import entelect.training.incubator.spring.authentication.model.User;
import entelect.training.incubator.spring.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
class UserController {
  private final UserService userService;

  @Autowired
  UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/login")
  ResponseEntity<?> loginUser(@RequestBody LoginRequest request)
      throws UserNotFoundException {
    try {
      if (request.getUsername() == null || request.getPassword() == null) {
        throw new CustomParameterConstraintException("Incorrect login details");
      }

      AuthResponse response = userService.authenticateUser(request);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (UserNotFoundException e) {
      throw new UserNotFoundException(e.getMessage());
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
    final User existingUser = userService.getUserByEmail(request.getEmail());
    if (existingUser != null ) {
      throw new DuplicateUserException("User with email " + request.getEmail() + " already exists. Cannot create duplicate user");
    }

    try {
      final User newUser = userService.saveUser(request);
      return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    } catch (Exception e) {
      throw new CustomParameterConstraintException(e.getMessage());
    }
  }
}
