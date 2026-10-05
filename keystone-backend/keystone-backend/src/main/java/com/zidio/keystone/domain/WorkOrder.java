package com.zidio.keystone.domain;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;



@Entity
@Table(name = "work_orders")
public class WorkOrder {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(
        name = "work_order_code",
        unique = true,
        nullable = false
    )
    private String workOrderCode;



    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;



    @Column(length = 1000)
    private String description;



    @NotBlank(message = "Priority is required")
    @Column(nullable = false)
    private String priority;



    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status;



    @NotNull(message = "SLA Due Date is required")
    @Column(name = "sla_due_date")
    private LocalDateTime slaDueDate;



    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;




    public WorkOrder() {

    }




    public WorkOrder(
            Long id,
            String workOrderCode,
            String title,
            String description,
            String priority,
            String status,
            LocalDateTime slaDueDate,
            LocalDateTime createdAt
    ) {

        this.id = id;
        this.workOrderCode = workOrderCode;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.slaDueDate = slaDueDate;
        this.createdAt = createdAt;

    }





    @PrePersist
    public void prePersist() {

        if(createdAt == null){

            createdAt = LocalDateTime.now();

        }

    }





    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
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



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}