package com.zidio.keystone.dto;

import java.time.LocalDateTime;

import com.zidio.keystone.domain.WorkOrder;

public class WorkOrderResponse {


    private Long id;
    private String workOrderCode;
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime slaDueDate;
    private LocalDateTime createdAt;


    public WorkOrderResponse() {
    }


    public WorkOrderResponse(WorkOrder workOrder) {

        this.id = workOrder.getId();
        this.workOrderCode = workOrder.getWorkOrderCode();
        this.title = workOrder.getTitle();
        this.description = workOrder.getDescription();
        this.priority = workOrder.getPriority();
        this.status = workOrder.getStatus();
        this.slaDueDate = workOrder.getSlaDueDate();
        this.createdAt = workOrder.getCreatedAt();

    }


    public Long getId() {
        return id;
    }


    public String getWorkOrderCode() {
        return workOrderCode;
    }


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public String getPriority() {
        return priority;
    }


    public String getStatus() {
        return status;
    }


    public LocalDateTime getSlaDueDate() {
        return slaDueDate;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}