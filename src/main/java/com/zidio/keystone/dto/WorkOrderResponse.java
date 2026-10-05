package com.zidio.keystone.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.LazyInitializationException;

import com.zidio.keystone.domain.SlaStatus;
import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.domain.WorkOrderStatus;

public class WorkOrderResponse {

    private Long id;

    private String workOrderCode;

    private String title;

    private String description;

    private String priority;

    private WorkOrderStatus status;

    private LocalDateTime slaDueDate;

    private LocalDateTime createdAt;

    private SlaStatus slaStatus;

    // =========================================================
    // TECHNICIAN DETAILS
    // =========================================================

    private Long technicianId;

    private String technicianName;

    private String technicianEmail;

    private String technicianRole;

    // =========================================================
    // CUSTOMER DETAILS
    // =========================================================

    private Long customerId;

    private String customerName;

    // =========================================================
    // SITE DETAILS
    // =========================================================

    private Long siteId;

    private String siteName;

    // =========================================================
    // F6 TOTALS
    // =========================================================

    private BigDecimal partsCost = BigDecimal.ZERO;

    private Integer labourMinutes = 0;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public WorkOrderResponse(WorkOrder workOrder) {

        this.id = workOrder.getId();

        this.workOrderCode = workOrder.getWorkOrderCode();

        this.title = workOrder.getTitle();

        this.description = workOrder.getDescription();

        this.priority = workOrder.getPriority();

        this.status = workOrder.getStatus();

        this.slaDueDate = workOrder.getSlaDueDate();

        this.createdAt = workOrder.getCreatedAt();

        // =====================================================
        // SLA STATUS
        // =====================================================

        if (workOrder.getSlaDueDate() == null) {

            this.slaStatus = SlaStatus.ON_TIME;

        } else {

            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(workOrder.getSlaDueDate())) {

                this.slaStatus = SlaStatus.BREACHED;

            } else if (now.plusHours(24)
                    .isAfter(workOrder.getSlaDueDate())) {

                this.slaStatus = SlaStatus.AT_RISK;

            } else {

                this.slaStatus = SlaStatus.ON_TIME;
            }
        }

        // =====================================================
        // TECHNICIAN DETAILS
        // =====================================================

        try {

            if (workOrder.getTechnician() != null) {

                this.technicianId =
                        workOrder.getTechnician().getId();

                this.technicianName =
                        workOrder.getTechnician().getName();

                this.technicianEmail =
                        workOrder.getTechnician().getEmail();

                this.technicianRole =
                        workOrder.getTechnician().getRole();
            }

        } catch (LazyInitializationException ex) {

            this.technicianId = null;
            this.technicianName = null;
            this.technicianEmail = null;
            this.technicianRole = null;
        }

        // =====================================================
        // CUSTOMER DETAILS
        // =====================================================

        try {

            if (workOrder.getCustomer() != null) {

                this.customerId =
                        workOrder.getCustomer().getId();

                this.customerName =
                        workOrder.getCustomer().getName();
            }

        } catch (LazyInitializationException ex) {

            this.customerId = null;
            this.customerName = null;
        }

        // =====================================================
        // SITE DETAILS
        // =====================================================

        try {

            if (workOrder.getSite() != null) {

                this.siteId =
                        workOrder.getSite().getId();

                this.siteName =
                        workOrder.getSite().getName();
            }

        } catch (LazyInitializationException ex) {

            this.siteId = null;
            this.siteName = null;
        }
    }

    // =========================================================
    // BASIC GETTERS
    // =========================================================

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

    public WorkOrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getSlaDueDate() {
        return slaDueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public SlaStatus getSlaStatus() {
        return slaStatus;
    }

    // =========================================================
    // TECHNICIAN GETTERS
    // =========================================================

    public Long getTechnicianId() {
        return technicianId;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public String getTechnicianEmail() {
        return technicianEmail;
    }

    public String getTechnicianRole() {
        return technicianRole;
    }

    // =========================================================
    // CUSTOMER GETTERS
    // =========================================================

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    // =========================================================
    // SITE GETTERS
    // =========================================================

    public Long getSiteId() {
        return siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    // =========================================================
    // F6 GETTERS
    // =========================================================

    public BigDecimal getPartsCost() {
        return partsCost;
    }

    public Integer getLabourMinutes() {
        return labourMinutes;
    }

    // =========================================================
    // F6 SETTERS
    // =========================================================

    public void setPartsCost(BigDecimal partsCost) {

        this.partsCost =
                partsCost != null
                        ? partsCost
                        : BigDecimal.ZERO;
    }

    public void setLabourMinutes(Integer labourMinutes) {

        this.labourMinutes =
                labourMinutes != null
                        ? labourMinutes
                        : 0;
    }
}