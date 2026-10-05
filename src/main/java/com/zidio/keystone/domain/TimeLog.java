package com.zidio.keystone.domain;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zidio.keystone.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "time_logs")
public class TimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer minutes;

    @Column(length = 1000)
    private String note;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;

    public TimeLog() {
    }

    @PrePersist
    public void prePersist() {
        if (loggedAt == null) {
            loggedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*
     * Keep the actual WorkOrder getter ignored so Jackson
     * does not serialize the complete WorkOrder entity graph.
     */
    @JsonIgnore
    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    /*
     * Return only the Work Order information needed by the frontend.
     */
    @JsonProperty("workOrder")
    public Object getWorkOrderForJson() {

        if (workOrder == null) {
            return null;
        }

        return Map.of(
                "id", workOrder.getId(),
                "workOrderCode", workOrder.getWorkOrderCode()
        );
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public User getTechnician() {
        return technician;
    }

    public void setTechnician(User technician) {
        this.technician = technician;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }
}