package com.zidio.keystone.repository;

import com.zidio.keystone.domain.TimeLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeLogRepository
        extends JpaRepository<TimeLog, Long> {

    List<TimeLog> findByWorkOrderId(
            Long workOrderId);

    List<TimeLog> findByTechnicianId(
            Long technicianId);

    // Total logged minutes for a work order
    @Query("""
            SELECT COALESCE(SUM(t.minutes), 0)
            FROM TimeLog t
            WHERE t.workOrder.id = :workOrderId
           """)
    Integer getTotalMinutesByWorkOrderId(
            @Param("workOrderId") Long workOrderId);
}

