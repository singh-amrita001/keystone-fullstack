package com.zidio.keystone.dto;

import java.time.LocalDateTime;


public class WorkOrderUpdateRequest {


    private String workOrderCode;

    private String title;

    private String description;

    private String priority;

    private String status;

    private LocalDateTime slaDueDate;



    public WorkOrderUpdateRequest() {
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