package entelect.training.incubator.spring.authentication.controller;

import entelect.training.incubator.spring.authentication.model.Role;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
  private String username;
  private String token;
  private Role role;
  private String message;
  private LocalDateTime expiryDate;

}
