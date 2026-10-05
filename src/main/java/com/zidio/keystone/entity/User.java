package com.zidio.keystone.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.zidio.keystone.domain.Customer;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // NAME
    // =====================================================

    @Column(nullable = false)
    private String name;

    // =====================================================
    // EMAIL
    // =====================================================

    @Column(unique = true, nullable = false)
    private String email;

    // =====================================================
    // PASSWORD
    // =====================================================

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    // =====================================================
    // ROLE
    // =====================================================

    @Column(nullable = false)
    private String role;

    // =====================================================
    // CUSTOMER ORGANIZATION
    // CUSTOMER USERS ONLY
    // =====================================================

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // =====================================================
    // PROFILE PICTURE
    // =====================================================

    @Column(name = "profile_pic")
    private String profilePic;

    // =====================================================
    // EMAIL NOTIFICATIONS
    // =====================================================

    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = true;

    // =====================================================
    // SYSTEM NOTIFICATIONS
    // =====================================================

    @Column(name = "system_notifications", nullable = false)
    private Boolean systemNotifications = true;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public User() {
    }

    // =====================================================
    // ID
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // =====================================================
    // NAME
    // =====================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // =====================================================
    // EMAIL
    // =====================================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // =====================================================
    // PASSWORD
    // =====================================================

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // =====================================================
    // ROLE
    // =====================================================

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // =====================================================
    // CUSTOMER
    // =====================================================

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // =====================================================
    // PROFILE PIC
    // =====================================================

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    // =====================================================
    // EMAIL NOTIFICATIONS
    // =====================================================

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    // =====================================================
    // SYSTEM NOTIFICATIONS
    // =====================================================

    public Boolean getSystemNotifications() {
        return systemNotifications;
    }

    public void setSystemNotifications(Boolean systemNotifications) {
        this.systemNotifications = systemNotifications;
    }
}

