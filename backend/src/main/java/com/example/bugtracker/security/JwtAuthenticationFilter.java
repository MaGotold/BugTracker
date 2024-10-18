package com.example.bugtracker.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.FilterChain;
import java.util.List;

import io.jsonwebtoken.Jwts;
import java.util.Collections;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.Claims;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse respone, FilterChain filterChain)
        throws ServletException, IOException {

            String path = request.getRequestURI();
            if(path.contains("/auth/sign-in") || path.contains("/auth/sign-up")) {
                filterChain.doFilter(request, respone);
                return;
            }

            String header = request.getHeader("Authorization");
            String token = null;
            if(token == null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }

            if(token != null) {
                try {
                    Claims claims = Jwts.parser()
                        .setSigningKey(jwtUtil.getSecretKey())
                        .parseClaimsJws(token)
                        .getBody();

                    String username = claims.getSubject();
                    long id = claims.get("id", long.class);
                    String email = claims.get("email", String.class);
                    String role = claims.get("role", String.class); 

                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);


                } catch(SignatureException e) {
                    respone.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
                    return;

                } catch(Exception e) {
                    respone.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
            }
        }
    
}
