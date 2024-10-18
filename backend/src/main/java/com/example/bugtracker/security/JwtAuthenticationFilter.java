package com.example.bugtracker.security;

import java.io.IOException;
import java.security.SignatureException;

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.GrantedAuthority;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.FilterChain;
import com.example.bugtracker.security.JwtUtil;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.Claims;



public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse respone, FilterChain filter)
        throws ServletException, IOException {
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
