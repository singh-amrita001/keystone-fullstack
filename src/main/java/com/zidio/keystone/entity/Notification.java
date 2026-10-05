package com.zidio.keystone.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // RECIPIENT
    // =====================================================

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // =====================================================
    // WORK ORDER
    // =====================================================

    @Column(name = "work_order_id")
    private Long workOrderId;

    // =====================================================
    // TYPE
    // =====================================================

    @Column(nullable = false)
    private String type;

    // =====================================================
    // MESSAGE
    // =====================================================

    @Column(nullable = false, length = 1000)
    private String message;

    // =====================================================
    // READ STATUS
    // =====================================================

    @Column(name = "is_read", nullable = false)
    private Boolean read = false;

    // =====================================================
    // CREATED AT
    // =====================================================

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public Notification() {
    }

    // =====================================================
    // PRE PERSIST
    // =====================================================

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (read == null) {
            read = false;
        }
    }

    // =====================================================
    // GET ID
    // =====================================================

    public Long getId() {
        return id;
    }

    // =====================================================
    // SET ID
    // =====================================================

    public void setId(Long id) {
        this.id = id;
    }

    // =====================================================
    // GET USER
    // =====================================================

    public User getUser() {
        return user;
    }

    // =====================================================
    // SET USER
    // =====================================================

    public void setUser(User user) {
        this.user = user;
    }

    // =====================================================
    // GET WORK ORDER ID
    // =====================================================

    public Long getWorkOrderId() {
        return workOrderId;
    }

    // =====================================================
    // SET WORK ORDER ID
    // =====================================================

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    // =====================================================
    // GET TYPE
    // =====================================================

    public String getType() {
        return type;
    }

    // =====================================================
    // SET TYPE
    // =====================================================

    public void setType(String type) {
        this.type = type;
    }

    // =====================================================
    // GET MESSAGE
    // =====================================================

    public String getMessage() {
        return message;
    }

    // =====================================================
    // SET MESSAGE
    // =====================================================

    public void setMessage(String message) {
        this.message = message;
    }

    // =====================================================
    // GET READ
    // =====================================================

    public Boolean getRead() {
        return read;
    }

    // =====================================================
    // SET READ
    // =====================================================

    public void setRead(Boolean read) {
        this.read = read;
    }

    // =====================================================
    // GET CREATED AT
    // =====================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // =====================================================
    // SET CREATED AT
    // =====================================================

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}