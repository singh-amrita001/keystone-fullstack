package com.zidio.keystone.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.UserRepository;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin("*")
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =====================================================
    // UPLOAD PROFILE PICTURE
    // =====================================================

    @PostMapping("/upload/{email}")
    public ResponseEntity<?> uploadProfilePic(
            @PathVariable String email,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        // Check file
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    "Please select a profile picture"
            );
        }

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Create uploads folder
        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Create unique file name
        String originalFileName = file.getOriginalFilename();

        String fileName =
                System.currentTimeMillis()
                + "_"
                + originalFileName;

        // Save file
        Path path = uploadPath.resolve(fileName);

        Files.write(
                path,
                file.getBytes()
        );

        System.out.println(
                "Saved file at: "
                + path.toAbsolutePath()
        );

        // Save image URL in database
        user.setProfilePic(
                "/uploads/" + fileName
        );

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
}