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
import java.util.Map;

import java.util.Collections;
import com.example.bugtracker.service.RedisService;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

            String path = request.getRequestURI();
            if(path.contains("/auth/sign-in") || path.contains("/auth/sign-up") || path.contains("/auth/refresh")) {
                filterChain.doFilter(request, response);
                return;
            }

            String header = request.getHeader("Authorization");
            String token = null;

            if(token == null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }

            if(token != null && redisService.isTokenBlacklisted(token) == true) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token is invalid.");
                return; 
            }

            
            Map<String, String> claims = jwtUtil.parseSubjectAndRole(token);

            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(claims.get("role")));
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(claims.get("subject"), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }
    
}
