package com.zidio.keystone.controller;

import com.zidio.keystone.dto.ChangePasswordRequest;
import com.zidio.keystone.dto.UserResponse;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================
    // GET ALL USERS
    // ==========================

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(

                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getProfilePic()

                ))
                .toList();

        return ResponseEntity.ok(users);
    }

    // ==========================
    // UPDATE PROFILE
    // ==========================

    @PutMapping("/profile/{email}")
    public ResponseEntity<?> updateProfile(

            @PathVariable String email,

            @RequestBody User updatedUser

    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        user.setName(updatedUser.getName());

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    // ==========================
    // CHANGE PASSWORD
    // ==========================

    @PutMapping("/change-password/{email}")
    public ResponseEntity<?> changePassword(

            @PathVariable String email,

            @RequestBody ChangePasswordRequest request

    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        userRepository.save(user);

        return ResponseEntity.ok(
                "Password updated successfully"
        );
    }

    // ==========================
    // UPLOAD PROFILE IMAGE
    // ==========================

    @PostMapping("/profile/upload/{email}")
    public ResponseEntity<?> uploadProfilePic(

            @PathVariable String email,

            @RequestParam("file") MultipartFile file

    ) throws IOException {

        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {

            Files.createDirectories(uploadPath);
        }

        String fileName =

                System.currentTimeMillis()
                        + "_"
                        + file.getOriginalFilename();

        Path filePath = uploadPath.resolve(fileName);

        Files.write(

                filePath,

                file.getBytes()

        );

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        user.setProfilePic(
                "/uploads/" + fileName
        );

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
}