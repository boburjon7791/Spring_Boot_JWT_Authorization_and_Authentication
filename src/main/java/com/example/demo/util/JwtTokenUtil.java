package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {
    private static final String SECRET_KEY = "6DB21BA62A6AD2A946DFD9CB3734A503CB41225CA2523C85D13EBBA3211533D2";


    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setIssuer("http://localhost:8080")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
    public Key key(){
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

    public boolean isValid(@NonNull String token) {
        try {
            Claims claims = Jwts.parserBuilder() .
                    setSigningKey(key()) .build() .
                    parseClaimsJws(token) .getBody();
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        }catch (Exception e) {
            throw e;
        }
    }

    public String getUsername(@NonNull String token) {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(key())
                .build().parseClaimsJws(token)
                .getBody();
        return body.getSubject();
    }
}
