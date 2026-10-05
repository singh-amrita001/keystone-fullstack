package com.zidio.keystone.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PartUsageRequest {

    // =========================================================
    // PART ID
    // =========================================================

    @NotNull(message = "Part ID is required")
    private Long partId;

    // =========================================================
    // WORK ORDER ID
    // =========================================================

    @NotNull(message = "Work order ID is required")
    private Long workOrderId;

    // =========================================================
    // QUANTITY
    // =========================================================

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PartUsageRequest() {
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public Long getPartId() {
        return partId;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

