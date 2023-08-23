package com.example.demo.service;

import com.example.demo.AuthUser;
import com.example.demo.AuthUserRepository;
import com.example.demo.exception.IllegalParamException;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    public CustomUserDetailsService(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AuthUser> byUsername = authUserRepository.findByUsername(username);
        AuthUser authUser = byUsername.orElseThrow(IllegalParamException::new);
        return User.builder().username(authUser.getUsername())
                .password(authUser.getPassword())
                .accountLocked(false)
                .accountExpired(false)
                .roles(authUser.getRole())
                .credentialsExpired(false)
                .build();
    }
}
