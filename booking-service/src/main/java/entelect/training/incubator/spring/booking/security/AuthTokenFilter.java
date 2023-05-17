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
import java.util.List;
import java.util.Objects;
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
            Claims claims = Jwts.parser().setSigningKey("secret").parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            List<String> userAuth;
            if(username != null){
                ArrayList authority = (ArrayList) claims.get("authorities");
                LinkedHashMap<String, String> authorities = (LinkedHashMap<String, String>)authority.get(0);
                List<String> authorities = (List<String>) authority.get(0).;
                authorities.forEach(authority -> userAuth.add(authority("authority")));

                LinkedHashMap<String, String> authority = (LinkedHashMap<String, String>) claims.get("authorities").get(0);
                List<String> authorities = (List<String>) authority.values();

                //Create auth object
                // UsernamePasswordAuthenticationToken: A built-in object, used by spring to represent the current authenticated / being authenticated user.
                // It needs a list of authorities, which has type of GrantedAuthority interface, where SimpleGrantedAuthority is an implementation of that interface
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null,
                                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

                //authenticate user
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
//            request.setAttribute("claims", claims);
//            request.setAttribute("booking", request.getParameter("id"));

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
