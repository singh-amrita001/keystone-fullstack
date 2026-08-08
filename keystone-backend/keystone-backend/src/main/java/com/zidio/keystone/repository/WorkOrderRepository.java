package com.zidio.keystone.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zidio.keystone.domain.WorkOrder;



public interface WorkOrderRepository 
        extends JpaRepository<WorkOrder, Long> {



    // Find work orders by status with pagination

    Page<WorkOrder> findByStatus(
            String status,
            Pageable pageable
    );



    // Count orders by single status

    long countByStatus(
            String status
    );



    // Count orders by multiple statuses
    // Example: OPEN + IN_PROGRESS

    long countByStatusIn(
            List<String> statuses
    );



    // Count orders by priority

    long countByPriority(
            String priority
    );



    // Latest 5 work orders for dashboard activity

    List<WorkOrder> findTop5ByOrderByCreatedAtDesc();


}