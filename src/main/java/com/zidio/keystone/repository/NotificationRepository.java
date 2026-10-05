package com.zidio.keystone.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zidio.keystone.entity.Notification;
import com.zidio.keystone.entity.User;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // =====================================================
    // GET USER NOTIFICATIONS - PAGINATED
    // =====================================================

    Page<Notification> findByUserOrderByCreatedAtDesc(
            User user,
            Pageable pageable
    );

    // =====================================================
    // GET UNREAD NOTIFICATIONS
    // =====================================================

    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(
            User user
    );

    // =====================================================
    // COUNT UNREAD NOTIFICATIONS
    // =====================================================

    long countByUserAndReadFalse(User user);

    // =====================================================
    // CHECK DUPLICATE NOTIFICATION
    // =====================================================

    boolean existsByUserAndWorkOrderIdAndType(
            User user,
            Long workOrderId,
            String type
    );
}

