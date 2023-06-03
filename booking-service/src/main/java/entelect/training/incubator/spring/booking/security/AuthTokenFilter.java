package entelect.training.incubator.spring.booking.security;

import entelect.training.incubator.spring.booking.exceptions.JwtTokenMalformedException;
import entelect.training.incubator.spring.booking.exceptions.JwtTokenMissingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.noFilterPaths}")
  private String[] pathsToIgnore;

  @Value("${jwt.authority}")
  private String authority;


  @Override
  protected void doFilterInternal(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final FilterChain filterChain)
      throws ServletException, IOException {
    final String token = parseToken(request);

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
    }
    catch (MalformedJwtException | IllegalArgumentException e) {
      throw new JwtTokenMalformedException("Invalid JWT token");
    }
    catch (JwtException e) {
      SecurityContextHolder.clearContext();
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request)
      throws ServletException {
    return Boolean.TRUE.equals(ignoreFilter(request.getRequestURI()));
  }

  private boolean ignoreFilter(String requestURI){
    return Arrays.stream(pathsToIgnore).anyMatch(path -> requestURI.contains(path));
  }

  private String parseToken(final HttpServletRequest request) {
    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new JwtTokenMissingException("No JWT token found in request headers");
    }
    return authHeader.substring(7);
  }
}
