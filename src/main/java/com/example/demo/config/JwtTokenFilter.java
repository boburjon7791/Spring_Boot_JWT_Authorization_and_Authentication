package com.example.demo.config;

import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {
    final CustomUserDetailsService service;
    final JwtTokenUtil jwtTokenUtil;
    public JwtTokenFilter(CustomUserDetailsService service, JwtTokenUtil jwtTokenUtil) {
        this.service = service;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request
            ,@NonNull HttpServletResponse response
            ,@NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization==null || authorization.isBlank()) {
            filterChain.doFilter(request,response);
            return;
        }
        System.out.println("authorization = " + authorization);
        authorization=authorization.substring(7);
        if (!jwtTokenUtil.isValid(authorization)) {
            filterChain.doFilter(request,response);
            return;
        }
        String username = jwtTokenUtil.getUsername(authorization);
//        username = username.substring(7);

        UserDetails userDetails = service.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(username,null,userDetails.getAuthorities());
        WebAuthenticationDetails details = new WebAuthenticationDetails(request);
        System.out.println("details = " + details);
        authenticationToken.setDetails(details);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authenticationToken);
        filterChain.doFilter(request,response);
    }
}
