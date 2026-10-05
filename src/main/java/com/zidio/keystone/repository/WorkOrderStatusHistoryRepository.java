package com.zidio.keystone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zidio.keystone.domain.WorkOrderStatusHistory;

public interface WorkOrderStatusHistoryRepository
        extends JpaRepository<WorkOrderStatusHistory, Long> {

    List<WorkOrderStatusHistory> findByWorkOrderIdOrderByChangedAtDesc(Long workOrderId);
}