package com.zidio.keystone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zidio.keystone.dto.ApiResponse;
import com.zidio.keystone.dto.LoginRequest;
import com.zidio.keystone.dto.LoginResponse;
import com.zidio.keystone.dto.RegisterRequest;
import com.zidio.keystone.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


private final AuthService authService;

public AuthController(AuthService authService) {
    this.authService = authService;
}

@PostMapping("/register")
public ResponseEntity<?> register(
        @RequestBody RegisterRequest request) {

    try {

        authService.register(request);

        return ResponseEntity.ok(
                new ApiResponse(
                    "User registered successfully"
                )
        );


    } catch(RuntimeException e) {


        return ResponseEntity
                .badRequest()
                .body(
                    new ApiResponse(
                        e.getMessage()
                    )
                );

    }

}

@PostMapping("/login")
public ResponseEntity<LoginResponse> login(
        @RequestBody LoginRequest request) {


    LoginResponse response =
            authService.login(
                    request.getEmail(),
                    request.getPassword()
            );


    return ResponseEntity.ok(response);
}


}
