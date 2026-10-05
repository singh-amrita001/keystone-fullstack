package com.zidio.keystone.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.zidio.keystone.entity.Notification;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.UserRepository;
import com.zidio.keystone.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    private final UserRepository userRepository;

    public NotificationController(
            NotificationService notificationService,
            UserRepository userRepository
    ) {

        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    // =====================================================
    // GET ALL NOTIFICATIONS - PAGINATED
    // =====================================================

    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            Authentication authentication,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size
    ) {

        User user = getLoggedInUser(authentication);

        return ResponseEntity.ok(
                notificationService.getUserNotifications(
                        user,
                        page,
                        size
                )
        );
    }

    // =====================================================
    // GET UNREAD NOTIFICATIONS
    // =====================================================

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            Authentication authentication
    ) {

        User user = getLoggedInUser(authentication);

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(user)
        );
    }

    // =====================================================
    // GET UNREAD NOTIFICATION COUNT
    // =====================================================

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(
            Authentication authentication
    ) {

        User user = getLoggedInUser(authentication);

        return ResponseEntity.ok(
                notificationService.getUnreadCount(user)
        );
    }

    // =====================================================
    // MARK ONE NOTIFICATION AS READ
    // =====================================================

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User user = getLoggedInUser(authentication);

        return ResponseEntity.ok(
                notificationService.markAsRead(
                        id,
                        user
                )
        );
    }

    // =====================================================
    // MARK ALL NOTIFICATIONS AS READ
    // =====================================================

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            Authentication authentication
    ) {

        User user = getLoggedInUser(authentication);

        notificationService.markAllAsRead(user);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // GET LOGGED-IN USER
    // =====================================================

    private User getLoggedInUser(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Logged-in user not found"
                        )
                );
    }
}

