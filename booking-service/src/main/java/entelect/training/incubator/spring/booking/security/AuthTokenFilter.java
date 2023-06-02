package entelect.training.incubator.spring.booking.security;

import entelect.training.incubator.spring.booking.exceptions.JwtTokenMalformedException;
import entelect.training.incubator.spring.booking.exceptions.JwtTokenMissingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.authority}")
  private String authority;
  @Override
  protected void doFilterInternal(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final FilterChain filterChain)
      throws ServletException, IOException {
    final String token = parseToken(request);
    if (token == null) {
      filterChain.doFilter(request, response);
      throw new JwtTokenMalformedException("Invalid JWT token");
    }
    try {
      //validate token
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(secret)
          .build()
          .parseClaimsJws(token)
          .getBody();
      String username = claims.getSubject();
      if (username != null) {
        ArrayList appAuthority = (ArrayList) claims.get(authority);
        LinkedHashMap<String, String> authorities =
            (LinkedHashMap<String, String>) appAuthority.get(0);
        //Create auth object
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(username, null,
                authorities.values().stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        //authenticate user
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    } catch (JwtException | IllegalArgumentException e) {
      SecurityContextHolder.clearContext();
    }
    filterChain.doFilter(request, response);
  }

  private String parseToken(final HttpServletRequest request) {
    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new JwtTokenMissingException("No JWT token found in request headers");
    }
    return authHeader.substring(7);
  }
}
