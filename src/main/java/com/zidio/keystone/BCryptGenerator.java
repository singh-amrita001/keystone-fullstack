package com.zidio.keystone;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String hash = encoder.encode("tech123");

        System.out.println("BCrypt Hash:");
        System.out.println(hash);
    }
}