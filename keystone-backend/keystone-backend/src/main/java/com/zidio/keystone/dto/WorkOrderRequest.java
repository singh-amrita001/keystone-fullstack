package com.zidio.keystone.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WorkOrderRequest {


    @NotBlank(message = "Work Order Code is required")
    private String workOrderCode;


    @NotBlank(message = "Title is required")
    private String title;


    @NotBlank(message = "Description is required")
    private String description;


    @NotNull(message = "Priority is required")
    private String priority;


    @NotNull(message = "Status is required")
    private String status;


    private LocalDateTime slaDueDate;


    public WorkOrderRequest() {
    }


    public String getWorkOrderCode() {
        return workOrderCode;
    }


    public void setWorkOrderCode(String workOrderCode) {
        this.workOrderCode = workOrderCode;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getPriority() {
        return priority;
    }


    public void setPriority(String priority) {
        this.priority = priority;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDateTime getSlaDueDate() {
        return slaDueDate;
    }


    public void setSlaDueDate(LocalDateTime slaDueDate) {
        this.slaDueDate = slaDueDate;
    }
}
