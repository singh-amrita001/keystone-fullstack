package com.zidio.keystone.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zidio.keystone.domain.SlaStatus;
import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.domain.WorkOrderStatus;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.WorkOrderRepository;

@Service
public class SlaMonitoringService {

    private final WorkOrderRepository workOrderRepository;

    private final NotificationService notificationService;

    public SlaMonitoringService(
            WorkOrderRepository workOrderRepository,
            NotificationService notificationService
    ) {
        this.workOrderRepository = workOrderRepository;
        this.notificationService = notificationService;
    }

    // =====================================================
    // GET SLA STATUS
    // =====================================================

    public SlaStatus getSlaStatus(WorkOrder workOrder) {

        if (workOrder.getSlaDueDate() == null) {
            return SlaStatus.ON_TIME;
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(workOrder.getSlaDueDate())) {
            return SlaStatus.BREACHED;
        }

        if (now.plusHours(24).isAfter(workOrder.getSlaDueDate())) {
            return SlaStatus.AT_RISK;
        }

        return SlaStatus.ON_TIME;
    }

    // =====================================================
    // FIND BREACHED WORK ORDERS
    // =====================================================

    public List<WorkOrder> findBreachedWorkOrders() {

        LocalDateTime now = LocalDateTime.now();

        return workOrderRepository
                .findBySlaDueDateBeforeAndStatusNotIn(
                        now,
                        List.of(
                                WorkOrderStatus.COMPLETED,
                                WorkOrderStatus.CLOSED,
                                WorkOrderStatus.CANCELLED
                        )
                );
    }

    // =====================================================
    // FIND AT-RISK WORK ORDERS
    // =====================================================

    public List<WorkOrder> findAtRiskWorkOrders() {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime riskTime = now.plusHours(24);

        return workOrderRepository
                .findBySlaDueDateBetweenAndStatusNotIn(
                        now,
                        riskTime,
                        List.of(
                                WorkOrderStatus.COMPLETED,
                                WorkOrderStatus.CLOSED,
                                WorkOrderStatus.CANCELLED
                        )
                );
    }

    // =====================================================
    // AUTOMATIC SLA CHECK
    // Runs every 5 minutes
    // =====================================================

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkSlaStatusAutomatically() {

        List<WorkOrder> atRiskWorkOrders =
                findAtRiskWorkOrders();

        List<WorkOrder> breachedWorkOrders =
                findBreachedWorkOrders();

        // =================================================
        // AT-RISK NOTIFICATIONS
        // =================================================

        for (WorkOrder workOrder : atRiskWorkOrders) {

            createSlaNotification(
                    workOrder,
                    "SLA_AT_RISK",
                    "SLA for work order "
                            + workOrder.getWorkOrderCode()
                            + " is at risk and due within 24 hours."
            );
        }

        // =================================================
        // BREACHED NOTIFICATIONS
        // =================================================

        for (WorkOrder workOrder : breachedWorkOrders) {

            createSlaNotification(
                    workOrder,
                    "SLA_BREACHED",
                    "SLA for work order "
                            + workOrder.getWorkOrderCode()
                            + " has been breached."
            );
        }

        // =================================================
        // LOG SLA STATUS
        // =================================================

        if (!atRiskWorkOrders.isEmpty()) {

            System.out.println(
                    "SLA ALERT: "
                            + atRiskWorkOrders.size()
                            + " work order(s) are AT RISK."
            );
        }

        if (!breachedWorkOrders.isEmpty()) {

            System.out.println(
                    "SLA ALERT: "
                            + breachedWorkOrders.size()
                            + " work order(s) have BREACHED their SLA."
            );
        }

        if (atRiskWorkOrders.isEmpty()
                && breachedWorkOrders.isEmpty()) {

            System.out.println(
                    "SLA CHECK: No at-risk or breached work orders."
            );
        }
    }

    // =====================================================
    // CREATE SLA NOTIFICATION
    // =====================================================

    private void createSlaNotification(
            WorkOrder workOrder,
            String type,
            String message
    ) {

        // No technician assigned
        if (workOrder.getTechnician() == null) {
            return;
        }

        User technician = workOrder.getTechnician();

        // System notifications disabled
        if (Boolean.FALSE.equals(
                technician.getSystemNotifications()
        )) {

            System.out.println(
                    "SYSTEM NOTIFICATION DISABLED: "
                            + technician.getEmail()
            );

            return;
        }

        // Create notification
        // NotificationService handles duplicate checking
        notificationService.createNotification(
                technician,
                workOrder.getId(),
                type,
                message
        );
    }
}