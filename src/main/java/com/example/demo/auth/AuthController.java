package com.example.demo.auth;

import com.example.demo.AuthUser;
import com.example.demo.AuthUserRepository;
import com.example.demo.exception.IllegalParamException;
import com.example.demo.logger.AuthUserLogger;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.util.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthUserRepository authUserRepository;
    private final CustomUserDetailsService customUserDetails;

    public AuthController(PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil,
                          AuthUserRepository authUserRepository, CustomUserDetailsService customUserDetails) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authUserRepository = authUserRepository;
        this.customUserDetails = customUserDetails;
    }

    @PostMapping("/token")
    public String token(@RequestBody TokenRequest tokenRequest, HttpServletRequest req, HttpServletResponse res){
        String username = tokenRequest.username();
        String password = tokenRequest.password();
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);
        System.out.println(userDetails.getUsername()+" "+userDetails.getPassword()+" "+userDetails.getAuthorities());
        if (!passwordEncoder.matches(password,userDetails.getPassword())) {
            throw new IllegalParamException();
        }
        AuthUserLogger.tokenGave(userDetails);
        String token = jwtTokenUtil.generateToken(username);
                addCookie(req,res,token,"key");
                return token;
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthUserDto authUserDto){
        try {
            UserDetails userDetails =
                    customUserDetails.loadUserByUsername(authUserDto.username());
            if (Objects.equals(userDetails.getUsername(),authUserDto.username())) {
                throw new IllegalParamException();
            }
        }catch (Exception e){
            AuthUser authUser = AuthUser.builder()
                    .username(authUserDto.username())
                    .role(AuthUser.Role.CLIENT.name())
                    .password(passwordEncoder.encode(authUserDto.password()))
                    .build();
            authUserRepository.save(authUser);
            AuthUserLogger.userRegistered(authUser);
            return new ResponseEntity<>("Successfully saved", HttpStatus.CREATED);
        }
        return null;
    }
    private static void addCookie(HttpServletRequest req, HttpServletResponse res, String token, String key) {
        Cookie[] cookies = req.getCookies();
        if (Objects.isNull(cookies)) {
            Cookie cookie = new Cookie(key, token);
            cookie.setMaxAge(24*60*60);
            cookie.setSecure(false);
            res.addCookie(cookie);
        }else {
            Optional<Cookie> first = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(key))
                    .findFirst();
            Cookie cookie = first.orElseThrow();
            cookie.setSecure(false);
            cookie.setMaxAge(24*60*60);
            res.addCookie(cookie);
        }
    }
    private static void removeCookie(HttpServletRequest req,HttpServletResponse res,String name){
        Cookie[] cookies = req.getCookies();
        if (!Objects.isNull(cookies)) {
            Optional<Cookie> first = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(name))
                    .findFirst();
            Cookie cookie = first.orElseThrow();
            cookie.setMaxAge(-1);
            cookie.setValue(null);
            res.addCookie(cookie);
        }
    }
}
