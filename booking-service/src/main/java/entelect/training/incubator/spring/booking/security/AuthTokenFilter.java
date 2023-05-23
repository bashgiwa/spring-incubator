package entelect.training.incubator.spring.booking.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token =  parseToken(request);
        if(token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try{
            //validate token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey("secret")
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            if(username != null){
                ArrayList appAuthority = (ArrayList) claims.get("app-auth");
                LinkedHashMap<String, String> authorities = (LinkedHashMap<String, String>) appAuthority.get(0);
                //Create auth object
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null,
                                authorities.values().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                //authenticate user
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }catch (JwtException | IllegalArgumentException e){
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7, authHeader.length());
        }
        return null;
    }
}
