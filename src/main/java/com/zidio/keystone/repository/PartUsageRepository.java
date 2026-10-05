package com.zidio.keystone.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zidio.keystone.domain.PartUsage;

public interface PartUsageRepository extends JpaRepository<PartUsage, Long> {

    // =========================================================
    // FIND PART USAGES BY PART ID
    // =========================================================

    List<PartUsage> findByPartId(Long partId);

    // =========================================================
    // FIND PART USAGES BY WORK ORDER ID
    // =========================================================

    List<PartUsage> findByWorkOrderId(Long workOrderId);

    // =========================================================
    // PAGINATED PART USAGES
    // =========================================================

    Page<PartUsage> findAll(Pageable pageable);

    // =========================================================
    // F6 - TOTAL PARTS COST BY WORK ORDER
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(pu.totalCost), 0)
        FROM PartUsage pu
        WHERE pu.workOrder.id = :workOrderId
    """)
    BigDecimal getTotalPartsCostByWorkOrderId(
            @Param("workOrderId") Long workOrderId
    );
}