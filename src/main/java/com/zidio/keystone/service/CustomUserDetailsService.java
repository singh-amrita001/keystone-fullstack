package com.zidio.keystone.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.UserRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                    () -> new UsernameNotFoundException(
                            "User not found: " + email
                    )
                );

        System.out.println(
                "EMAIL FROM DB : "
                + user.getEmail()
        );

        System.out.println(
                "ROLE FROM DB : "
                + user.getRole()
        );

        System.out.println(
                "USER ID FROM DB : "
                + user.getId()
        );

        String role = user.getRole();

        // Remove ROLE_ if already stored in database
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(
                        user.getEmail()
                )
                .password(
                        user.getPassword()
                )
                .roles(
                        role
                )
                .build();
    }
}