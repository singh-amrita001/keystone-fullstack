package com.zidio.keystone.dto;

public class NotificationPreferencesRequest {

    private Boolean emailNotifications;
    private Boolean systemNotifications;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public NotificationPreferencesRequest() {
    }

    public NotificationPreferencesRequest(
            Boolean emailNotifications,
            Boolean systemNotifications
    ) {
        this.emailNotifications = emailNotifications;
        this.systemNotifications = systemNotifications;
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