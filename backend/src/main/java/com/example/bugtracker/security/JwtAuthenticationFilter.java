package com.example.bugtracker.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.bugtracker.model.Permission;
import com.example.bugtracker.model.User;
import com.example.bugtracker.repository.PermissionRepository;
import com.example.bugtracker.service.RedisService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    RedisService redisService;
    @Autowired
    PermissionRepository permissionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

            String path = request.getRequestURI();
            if(path.contains("/auth/sign-in") || path.contains("/auth/sign-up") || path.contains("/auth/refresh") || path.contains("/index.html")) {
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

            List<GrantedAuthority> authorities = permissionRepository
                .findPermissionByRoleId(Long.parseLong(claims.get("role"))).stream()
                .map(Permission -> new SimpleGrantedAuthority(Permission.getPermission()))
                .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(claims.get("subject"), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }
    
}
