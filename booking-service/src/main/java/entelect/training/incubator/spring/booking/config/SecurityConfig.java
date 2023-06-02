package entelect.training.incubator.spring.booking.config;

import entelect.training.incubator.spring.booking.security.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final AuthTokenFilter authTokenFilter;

  public SecurityConfig(AuthTokenFilter authTokenFilter) {
    this.authTokenFilter = authTokenFilter;
  }

  @Bean
  SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeHttpRequests()
        .requestMatchers(HttpMethod.GET, "/bookings/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/bookings/**").permitAll()
        .anyRequest().authenticated()
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

