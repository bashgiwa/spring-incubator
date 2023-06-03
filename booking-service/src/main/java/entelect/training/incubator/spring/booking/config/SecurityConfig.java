package entelect.training.incubator.spring.booking.config;

import entelect.training.incubator.spring.booking.security.AuthTokenFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@SecurityScheme(
    name= "Bearer authentication",
    type= SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class SecurityConfig {

  private final AuthTokenFilter authTokenFilter;

  public SecurityConfig(AuthTokenFilter authTokenFilter) {
    this.authTokenFilter = authTokenFilter;
  }

  @Bean
  SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeHttpRequests()
        .requestMatchers(HttpMethod.GET, "/bookings/**").authenticated()
        .requestMatchers(HttpMethod.POST, "/bookings/**").authenticated()
        .anyRequest().permitAll()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilterBefore(authTokenFilter,
            UsernamePasswordAuthenticationFilter.class)
        .httpBasic();
    return http.build();
  }

}

