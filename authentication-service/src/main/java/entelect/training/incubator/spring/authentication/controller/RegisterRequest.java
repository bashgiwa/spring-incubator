package entelect.training.incubator.spring.authentication.controller;

import entelect.training.incubator.spring.authentication.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  private String username;
  private String password;
  private String email;
  private Role role;
}
