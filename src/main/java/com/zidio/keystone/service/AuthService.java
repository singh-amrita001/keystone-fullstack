package com.zidio.keystone.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zidio.keystone.dto.LoginResponse;
import com.zidio.keystone.dto.RegisterRequest;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.UserRepository;
import com.zidio.keystone.security.JwtService;


@Service
public class AuthService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;



    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

    }



    // REGISTER

    public User register(RegisterRequest request) {


        if(userRepository.existsByEmail(
                request.getEmail()
        )) {

            throw new RuntimeException(
                    "Email already exists"
            );

        }



        User user = new User();


        user.setName(
            request.getName()
        );


        user.setEmail(
            request.getEmail()
        );


        user.setPassword(
            passwordEncoder.encode(
                request.getPassword()
            )
        );


        /*
         Public registration
         always creates USER
        */
        user.setRole("USER");
        
        // DEFAULT PROFILE IMAGE
        user.setProfilePic(
            "https://i.pravatar.cc/150"
        );



        return userRepository.save(user);

    }






    // LOGIN

    public LoginResponse login(
            String email,
            String password
    ) {


        authenticationManager.authenticate(

            new UsernamePasswordAuthenticationToken(
                    email,
                    password
            )

        );



        User user =
            userRepository.findByEmail(email)
            .orElseThrow(
                () -> new RuntimeException(
                    "User not found"
                )
            );



        String token = jwtService.generateToken(
                email,
                user.getRole()
        );



        LoginResponse response =
            new LoginResponse();



        response.setToken(token);



        response.setName(
            user.getName()
        );



        response.setEmail(
            user.getEmail()
        );



        response.setRole(
            user.getRole()
        );
        
        response.setProfilePic(
                user.getProfilePic()
            );



        return response;

    }


}