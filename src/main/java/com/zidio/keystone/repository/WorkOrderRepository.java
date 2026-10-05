package com.zidio.keystone.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.domain.WorkOrderStatus;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    // =========================================================
    // FIND BY STATUS
    // =========================================================

    Page<WorkOrder> findByStatus(
            WorkOrderStatus status,
            Pageable pageable);

    // =========================================================
    // FIND BY TECHNICIAN
    // =========================================================

    Page<WorkOrder> findByTechnicianId(
            Long technicianId,
            Pageable pageable);

    Page<WorkOrder> findByTechnicianIdAndStatus(
            Long technicianId,
            WorkOrderStatus status,
            Pageable pageable);

    // =========================================================
    // F9 - FIND BY CUSTOMER
    // =========================================================

    Page<WorkOrder> findByCustomerId(
            Long customerId,
            Pageable pageable);

    Page<WorkOrder> findByCustomerIdAndStatus(
            Long customerId,
            WorkOrderStatus status,
            Pageable pageable);

    // =========================================================
    // COUNT BY STATUS
    // =========================================================

    long countByStatusIn(
            List<WorkOrderStatus> statuses);

    // =========================================================
    // WORK ORDER CODE
    // =========================================================

    boolean existsByWorkOrderCode(
            String workOrderCode);

    // =========================================================
    // SLA - BREACHED
    // =========================================================

    List<WorkOrder> findBySlaDueDateBeforeAndStatusNotIn(
            LocalDateTime now,
            List<WorkOrderStatus> statuses);

    // =========================================================
    // SLA - AT RISK
    // =========================================================

    List<WorkOrder> findBySlaDueDateBetweenAndStatusNotIn(
            LocalDateTime now,
            LocalDateTime riskTime,
            List<WorkOrderStatus> statuses);

    // =========================================================
    // FIND ALL WITH TECHNICIAN AND SITE
    // =========================================================

    @Query("""
            SELECT DISTINCT w
            FROM WorkOrder w
            LEFT JOIN FETCH w.technician
            LEFT JOIN FETCH w.site
            """)
    List<WorkOrder> findAllWithTechnicianAndSite();

}