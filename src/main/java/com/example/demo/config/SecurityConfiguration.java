package com.example.demo.config;


import com.example.demo.dto.ErrorDto;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import org.springframework.context.annotation.Bean;

import java.util.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    static ObjectMapper mapper = new ObjectMapper();
    public final CustomUserDetailsService userDetailsService;
    public final JwtTokenUtil jwtTokenUtil;

    public SecurityConfiguration(CustomUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return   httpSecurity.csrf(AbstractHttpConfigurer::disable)
              .authorizeRequests()
              .requestMatchers("/api/auth/**")
              .permitAll()
              .anyRequest()
              .authenticated()
              .and()
              .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                  httpSecurityExceptionHandlingConfigurer
                          .accessDeniedHandler((request, response, accessDeniedException) -> {
                              String path = request.getRequestURI();
                              String message = accessDeniedException.getMessage();
                              int code = 403;
                              response.setStatus(code);
                              ErrorDto errorDto = new ErrorDto(path, message, code, new Date());
                              ServletOutputStream outputStream = response.getOutputStream();
                              mapper.writeValue(outputStream,errorDto);
                              outputStream.flush();
                              outputStream.close();
                          })
                          .authenticationEntryPoint((request, response, authException) -> {
                              String path = request.getRequestURI();
                              String message = authException.getMessage();
                              int code = 401;
                              ErrorDto errorDto = new ErrorDto(path, message, code,new Date());
                              ServletOutputStream outputStream = response.getOutputStream();
                              mapper.writeValue(outputStream,errorDto);
                              outputStream.flush();
                              outputStream.close();
                          });
              })
                .sessionManagement(configurer -> {
                    configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
              .addFilterBefore(new JwtTokenFilter(userDetailsService,jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    /*@Bean*/
    public CorsConfigurationSource source(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
           "http://95.123.145.78:8056",
           "http://95.123.145.77:8056",
           "http://95.123.145.79:8056",
           "http://localhost:8080",
           "0:0:0:0:0:0:0:1"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
}