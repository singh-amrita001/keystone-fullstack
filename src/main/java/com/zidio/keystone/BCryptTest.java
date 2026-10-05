package com.zidio.keystone;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();


        String rawPassword = "newpassword123";


        String hash =
        "$2a$10$MJ/3vrOuvZP.ZUs7VcLcE.QTI5fwNNSreC.BNeHY6RzQaaIO5P7m6";


        System.out.println(
                encoder.matches(
                        rawPassword,
                        hash
                )
        );

    }
}