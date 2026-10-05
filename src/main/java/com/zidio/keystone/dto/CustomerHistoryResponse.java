package com.zidio.keystone.dto;

import java.time.LocalDateTime;

import com.zidio.keystone.domain.WorkOrderStatus;

public class CustomerHistoryResponse {

    private WorkOrderStatus fromStatus;
    private WorkOrderStatus toStatus;
    private LocalDateTime changedAt;

    public CustomerHistoryResponse() {
    }

    public CustomerHistoryResponse(
            WorkOrderStatus fromStatus,
            WorkOrderStatus toStatus,
            LocalDateTime changedAt) {

        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedAt = changedAt;
    }

    public WorkOrderStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(WorkOrderStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public WorkOrderStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(WorkOrderStatus toStatus) {
        this.toStatus = toStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}