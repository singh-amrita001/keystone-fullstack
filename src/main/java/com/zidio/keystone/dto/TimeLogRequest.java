package com.zidio.keystone.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TimeLogRequest {

    // =========================================================
    // WORK ORDER ID
    // =========================================================

    @NotNull(message = "Work order is required")
    private Long workOrderId;

    // =========================================================
    // MINUTES
    // =========================================================

    @NotNull(message = "Minutes are required")
    @Min(value = 1, message = "Minutes must be at least 1")
    private Integer minutes;

    // =========================================================
    // NOTE
    // =========================================================

    private String note;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TimeLogRequest() {
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public String getNote() {
        return note;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

