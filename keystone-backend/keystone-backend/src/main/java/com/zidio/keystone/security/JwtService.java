package com.zidio.keystone.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;


@Service
public class JwtService {


    private final String SECRET_KEY =
            "mysecretkeymysecretkeymysecretkey123456";



    private Key getKey(){

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes()
        );

    }



    // Generate JWT Token
    public String generateToken(String email){


        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                    new Date(System.currentTimeMillis()+86400000)
                )
                .signWith(getKey())
                .compact();

    }



    // Extract email from JWT token
    public String extractUsername(String token){


        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey)getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }



    // Validate token
    public boolean validateToken(
            String token,
            UserDetails userDetails){


        String username = extractUsername(token);


        return username.equals(
                userDetails.getUsername()
        );

    }

}