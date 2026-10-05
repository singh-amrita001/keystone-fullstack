package com.zidio.keystone.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zidio.keystone.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "work_orders")
public class WorkOrder {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // CUSTOMER
    // =====================================================

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // =====================================================
    // WORK ORDER CODE
    // =====================================================

    @Column(
        name = "work_order_code",
        unique = true,
        nullable = false
    )
    private String workOrderCode;

    // =====================================================
    // TITLE
    // =====================================================

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    // =====================================================
    // DESCRIPTION
    // =====================================================

    @Column(length = 1000)
    private String description;

    // =====================================================
    // PRIORITY
    // =====================================================

    @NotBlank(message = "Priority is required")
    @Column(nullable = false)
    private String priority;

    // =====================================================
    // STATUS
    // =====================================================

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatus status;

    // =====================================================
    // SLA DUE DATE
    // =====================================================

    @NotNull(message = "SLA Due Date is required")
    @Column(name = "sla_due_date")
    private LocalDateTime slaDueDate;

    // =====================================================
    // SITE
    // =====================================================

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    // =====================================================
    // TECHNICIAN
    // =====================================================
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private User technician;

    // =====================================================
    // CREATED AT
    // =====================================================

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public WorkOrder() {

    }

    public WorkOrder(
            Long id,
            String workOrderCode,
            String title,
            String description,
            String priority,
            WorkOrderStatus status,
            LocalDateTime slaDueDate,
            Customer customer,
            Site site,
            LocalDateTime createdAt,
            User technician
    ) {

        this.id = id;
        this.workOrderCode = workOrderCode;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.slaDueDate = slaDueDate;
        this.customer = customer;
        this.site = site;
        this.createdAt = createdAt;
        this.technician = technician;
    }

    // =====================================================
    // PRE PERSIST
    // =====================================================

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = WorkOrderStatus.NEW;
        }
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
    // WORK ORDER CODE
    // =====================================================

    public String getWorkOrderCode() {
        return workOrderCode;
    }

    public void setWorkOrderCode(String workOrderCode) {
        this.workOrderCode = workOrderCode;
    }

    // =====================================================
    // TITLE
    // =====================================================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // =====================================================
    // DESCRIPTION
    // =====================================================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // =====================================================
    // PRIORITY
    // =====================================================

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    // =====================================================
    // STATUS
    // =====================================================

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    // =====================================================
    // SLA DUE DATE
    // =====================================================

    public LocalDateTime getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(LocalDateTime slaDueDate) {
        this.slaDueDate = slaDueDate;
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
    // SITE
    // =====================================================

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    // =====================================================
    // TECHNICIAN
    // =====================================================

    public User getTechnician() {
        return technician;
    }

    public void setTechnician(User technician) {
        this.technician = technician;
    }

    // =====================================================
    // CREATED AT
    // =====================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}