package com.pragma.bootcamp.api.security;

import com.pragma.bootcamp.api.dto.UserTokenData;
import com.pragma.bootcamp.model.report.exception.BadCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        return jwtProvider.validateToken(authToken)
                .filter(valid -> valid)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid or expired JWT token")))
                .then(jwtProvider.getClaimsFromToken(authToken))
                .map(this::buildAuthenticationFromClaims)
                .onErrorMap(JwtException.class, ex ->
                        new BadCredentialsException("Invalid JWT token " + ex.getMessage()))
                .onErrorMap(ex ->
                        new BadCredentialsException("Authentication failed " + ex.getMessage()));
    }

//    private Authentication buildAuthenticationFromClaims(Claims claims) {
//        String username = claims.getSubject();
//        String role = claims.get("role", String.class);
//
//        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
//        return new UsernamePasswordAuthenticationToken(username, null, authorities);
//    }

    private Authentication buildAuthenticationFromClaims(Claims claims) {
        String username = claims.getSubject();
        String role = claims.get("role", String.class);
        String document = claims.get("document", String.class);
        String name = claims.get("name", String.class);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        // Creamos un objeto con todos los datos del token
        UserTokenData principal = new UserTokenData(username, name, role, document);

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

}
