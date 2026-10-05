package com.zidio.keystone;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
	
	public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String rawPassword = "newpassword123";

        String dbHash = "PASTE_YOUR_DATABASE_HASH_HERE";

        System.out.println(
            encoder.matches(rawPassword, dbHash)
        );
    }

}
