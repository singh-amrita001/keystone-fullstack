package com.zidio.keystone.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zidio.keystone.entity.Notification;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.NotificationRepository;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository
    ) {
        this.notificationRepository = notificationRepository;
    }

    // =====================================================
    // CREATE NOTIFICATION
    // =====================================================

    public Notification createNotification(
            User user,
            Long workOrderId,
            String type,
            String message
    ) {

        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException(
                    "Notification type is required"
            );
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Notification message is required"
            );
        }

        // Prevent duplicate notifications
        if (workOrderId != null &&
                notificationRepository
                        .existsByUserAndWorkOrderIdAndType(
                                user,
                                workOrderId,
                                type
                        )) {

            return null;
        }

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setWorkOrderId(workOrderId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    // =====================================================
    // GET ALL USER NOTIFICATIONS - PAGINATED
    // =====================================================
  
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(
            User user,
            int page,
            int size
    ) {

        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        Pageable pageable = PageRequest.of(page, size);

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user, pageable);
    }
    

  
    

    // =====================================================
    // GET UNREAD NOTIFICATIONS
    // =====================================================

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(
            User user
    ) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User is required"
            );
        }

        return notificationRepository
                .findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    // =====================================================
    // GET UNREAD COUNT
    // =====================================================

    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User is required"
            );
        }

        return notificationRepository
                .countByUserAndReadFalse(user);
    }

    // =====================================================
    // MARK ONE NOTIFICATION AS READ
    // =====================================================

    public Notification markAsRead(
            Long notificationId,
            User user
    ) {

        if (notificationId == null) {
            throw new IllegalArgumentException(
                    "Notification ID is required"
            );
        }

        if (user == null) {
            throw new IllegalArgumentException(
                    "User is required"
            );
        }

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Notification not found"
                                )
                        );

        // Make sure user can only update
        // their own notification
        if (notification.getUser() == null ||
                notification.getUser().getId() == null ||
                !notification.getUser()
                        .getId()
                        .equals(user.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to update this notification"
            );
        }

        notification.setRead(true);

        return notificationRepository.save(notification);
    }

    // =====================================================
    // MARK ALL NOTIFICATIONS AS READ
    // =====================================================

    public void markAllAsRead(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User is required"
            );
        }

        List<Notification> notifications =
                notificationRepository
                        .findByUserAndReadFalseOrderByCreatedAtDesc(
                                user
                        );

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }
}

