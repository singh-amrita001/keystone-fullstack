package com.zidio.keystone.controller;

import com.zidio.keystone.dto.ChangePasswordRequest;
import com.zidio.keystone.dto.NotificationPreferencesRequest;
import com.zidio.keystone.dto.UserResponse;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // =====================================================
    // GET ALL USERS - PAGINATED
    // =====================================================

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        System.out.println(
                "USER API AUTH: "
                        + SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        System.out.println(
                "USER API AUTHORITIES: "
                        + SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getAuthorities()
        );

        Pageable pageable = PageRequest.of(page, size);

        Page<UserResponse> users = userRepository
                .findAll(pageable)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getProfilePic()
                ));

        return ResponseEntity.ok(users);
    }

    // =====================================================
    // GET ALL TECHNICIANS
    // =====================================================

    @GetMapping("/technicians")
    public ResponseEntity<List<UserResponse>> getAllTechnicians() {

        List<UserResponse> technicians =
                userRepository.findByRoleIgnoreCase("TECHNICIAN")
                        .stream()
                        .map(user -> new UserResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole(),
                                user.getProfilePic()
                        ))
                        .toList();

        return ResponseEntity.ok(technicians);
    }

    // =====================================================
    // CREATE USER
    // =====================================================

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody Map<String, String> request
    ) {

        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");

        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Name is required");
        }

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Email is required");
        }

        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Password is required");
        }

        if (role == null || role.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Role is required");
        }

        role = role.trim().toUpperCase();

        if (!List.of(
                "ADMIN",
                "MANAGER",
                "DISPATCHER",
                "TECHNICIAN",
                "CUSTOMER",
                "USER"
        ).contains(role)) {

            return ResponseEntity.badRequest()
                    .body("Invalid role");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        User user = new User();

        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);

        return ResponseEntity.ok(
                new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getProfilePic()
                )
        );
    }

    // =====================================================
    // UPDATE USER ROLE
    // =====================================================

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String role = request.get("role");

        if (role == null || role.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Role is required");
        }

        role = role.trim().toUpperCase();

        if (!List.of(
                "ADMIN",
                "MANAGER",
                "DISPATCHER",
                "TECHNICIAN",
                "CUSTOMER",
                "USER"
        ).contains(role)) {

            return ResponseEntity.badRequest()
                    .body("Invalid role");
        }

        user.setRole(role);

        userRepository.save(user);

        return ResponseEntity.ok(
                new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getProfilePic()
                )
        );
    }

    // =====================================================
    // UPDATE PROFILE
    // =====================================================

    @PutMapping("/profile/{email}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String email,
            @RequestBody User updatedUser
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setName(updatedUser.getName());

        userRepository.save(user);

        return ResponseEntity.ok(
                new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getProfilePic()
                )
        );
    }

    // =====================================================
    // GET PROFILE
    // =====================================================

    @GetMapping("/profile/{email}")
    public ResponseEntity<?> getProfile(
            @PathVariable String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return ResponseEntity.ok(
                new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getProfilePic()
                )
        );
    }

    // =====================================================
    // CHANGE PASSWORD
    // =====================================================

    @PutMapping("/change-password/{email}")
    public ResponseEntity<?> changePassword(
            @PathVariable String email,
            @RequestBody ChangePasswordRequest request
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

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

    // =====================================================
    // UPLOAD PROFILE IMAGE
    // =====================================================

    @PostMapping(
            value = "/profile/upload/{email}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadProfilePic(
            @PathVariable String email,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Please select a file");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName =
                file.getOriginalFilename();

        if (originalFileName == null ||
                originalFileName.isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Invalid file name");
        }

        String fileName =
                System.currentTimeMillis()
                        + "_"
                        + originalFileName;

        Path filePath =
                uploadPath.resolve(fileName);

        Files.write(
                filePath,
                file.getBytes()
        );

        user.setProfilePic(
                "/uploads/" + fileName
        );

        userRepository.save(user);

        return ResponseEntity.ok(
                new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getProfilePic()
                )
        );
    }

    // =====================================================
    // GET NOTIFICATION PREFERENCES
    // =====================================================

    @GetMapping("/notification-preferences/{email}")
    public ResponseEntity<?> getNotificationPreferences(
            @PathVariable String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Map<String, Boolean> preferences =
                new HashMap<>();

        preferences.put(
                "emailNotifications",
                user.getEmailNotifications()
        );

        preferences.put(
                "systemNotifications",
                user.getSystemNotifications()
        );

        return ResponseEntity.ok(preferences);
    }

    // =====================================================
    // UPDATE NOTIFICATION PREFERENCES
    // =====================================================

    @PutMapping("/notification-preferences/{email}")
    public ResponseEntity<?> updateNotificationPreferences(
            @PathVariable String email,
            @RequestBody NotificationPreferencesRequest request
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (request.getEmailNotifications() != null) {

            user.setEmailNotifications(
                    request.getEmailNotifications()
            );
        }

        if (request.getSystemNotifications() != null) {

            user.setSystemNotifications(
                    request.getSystemNotifications()
            );
        }

        userRepository.save(user);

        return ResponseEntity.ok(
                "Notification preferences updated successfully"
        );
    }
}

