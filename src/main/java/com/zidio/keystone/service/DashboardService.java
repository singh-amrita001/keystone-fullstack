package com.zidio.keystone.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.domain.WorkOrderStatus;
import com.zidio.keystone.domain.WorkOrderStatusHistory;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.UserRepository;
import com.zidio.keystone.repository.WorkOrderRepository;
import com.zidio.keystone.repository.WorkOrderStatusHistoryRepository;

@Service
public class DashboardService {

    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;
    private final WorkOrderStatusHistoryRepository statusHistoryRepository;

    public DashboardService(
            WorkOrderRepository workOrderRepository,
            UserRepository userRepository,
            WorkOrderStatusHistoryRepository statusHistoryRepository) {

        this.workOrderRepository = workOrderRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    // =====================================================
    // MAIN DASHBOARD
    // =====================================================

    public Map<String, Object> getDashboardStats(
            String status,
            String priority,
            Long technicianId,
            Long siteId) {

        Map<String, Object> stats = new LinkedHashMap<>();

        // =====================================================
        // LOAD ALL WORK ORDERS
        // =====================================================

        List<WorkOrder> allOrders =
                workOrderRepository.findAllWithTechnicianAndSite();

        // =====================================================
        // APPLY DASHBOARD FILTERS
        // =====================================================

        List<WorkOrder> filteredOrders = allOrders.stream()

                // STATUS FILTER
                .filter(order ->
                        status == null ||
                        status.isBlank() ||
                        (
                            order.getStatus() != null &&
                            order.getStatus()
                                    .name()
                                    .equalsIgnoreCase(status)
                        )
                )

                // PRIORITY FILTER
                .filter(order ->
                        priority == null ||
                        priority.isBlank() ||
                        (
                            order.getPriority() != null &&
                            order.getPriority()
                                    .equalsIgnoreCase(priority)
                        )
                )

                // TECHNICIAN FILTER
                .filter(order ->
                        technicianId == null ||
                        (
                            order.getTechnician() != null &&
                            order.getTechnician().getId() != null &&
                            order.getTechnician()
                                    .getId()
                                    .equals(technicianId)
                        )
                )

                // SITE FILTER
                .filter(order ->
                        siteId == null ||
                        (
                            order.getSite() != null &&
                            order.getSite().getId() != null &&
                            order.getSite()
                                    .getId()
                                    .equals(siteId)
                        )
                )

                .toList();

        // =====================================================
        // BASIC COUNTS
        // =====================================================

        long totalOrders = filteredOrders.size();

        long completedOrders = filteredOrders.stream()
                .filter(order ->
                        order.getStatus() == WorkOrderStatus.COMPLETED)
                .count();

        long pendingOrders = filteredOrders.stream()
                .filter(order ->
                        order.getStatus() != WorkOrderStatus.COMPLETED &&
                        order.getStatus() != WorkOrderStatus.CLOSED &&
                        order.getStatus() != WorkOrderStatus.CANCELLED)
                .count();

        // Total platform users.
        // This does not change with work-order filters.
        long users = userRepository.count();

        // =====================================================
        // SLA COUNTS
        // =====================================================

        LocalDateTime now = LocalDateTime.now();

        List<WorkOrderStatus> closedStatuses =
                Arrays.asList(
                        WorkOrderStatus.COMPLETED,
                        WorkOrderStatus.CLOSED,
                        WorkOrderStatus.CANCELLED
                );

        // =====================================================
        // SLA BREACHED
        // =====================================================

        long breachedOrders = filteredOrders.stream()
                .filter(order ->
                        order.getSlaDueDate() != null)
                .filter(order ->
                        order.getSlaDueDate().isBefore(now))
                .filter(order ->
                        !closedStatuses.contains(order.getStatus()))
                .count();

        // =====================================================
        // SLA AT RISK
        // =====================================================

        LocalDateTime riskTime =
                now.plusHours(24);

        long atRiskOrders = filteredOrders.stream()
                .filter(order ->
                        order.getSlaDueDate() != null)
                .filter(order ->
                        !order.getSlaDueDate().isBefore(now))
                .filter(order ->
                        !order.getSlaDueDate().isAfter(riskTime))
                .filter(order ->
                        !closedStatuses.contains(order.getStatus()))
                .count();

        // =====================================================
        // SLA COMPLIANCE
        // =====================================================

        long slaApplicableOrders = 0;
        long slaCompliantOrders = 0;

        for (WorkOrder order : filteredOrders) {

            // No SLA date = cannot calculate compliance
            if (order.getSlaDueDate() == null) {
                continue;
            }

            // Cancelled work orders are excluded
            if (order.getStatus() == WorkOrderStatus.CANCELLED) {
                continue;
            }

            // Only completed/closed work orders can be evaluated
            if (order.getStatus() != WorkOrderStatus.COMPLETED &&
                    order.getStatus() != WorkOrderStatus.CLOSED) {
                continue;
            }

            slaApplicableOrders++;

            List<WorkOrderStatusHistory> history =
                    statusHistoryRepository
                            .findByWorkOrderIdOrderByChangedAtDesc(
                                    order.getId()
                            );

            // Find the latest COMPLETED transition
            WorkOrderStatusHistory completedHistory =
                    history.stream()
                            .filter(item ->
                                    item.getToStatus() ==
                                            WorkOrderStatus.COMPLETED)
                            .findFirst()
                            .orElse(null);

            // Completed before or exactly at SLA deadline
            if (completedHistory != null &&
                    completedHistory.getChangedAt() != null &&
                    !completedHistory.getChangedAt()
                            .isAfter(order.getSlaDueDate())) {

                slaCompliantOrders++;
            }
        }

        double slaCompliance =
                slaApplicableOrders > 0
                        ? Math.round(
                                (slaCompliantOrders * 10000.0)
                                        / slaApplicableOrders
                          ) / 100.0
                        : 0.0;

        // =====================================================
        // TECHNICIAN BREAKDOWN
        // =====================================================

        List<Map<String, Object>> technicianBreakdown =
                buildTechnicianBreakdown(filteredOrders);

        // =====================================================
        // SITE BREAKDOWN
        // =====================================================

        List<Map<String, Object>> siteBreakdown =
                buildSiteBreakdown(filteredOrders);

        // =====================================================
        // RESPONSE
        // =====================================================

        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("users", users);

        stats.put("atRiskOrders", atRiskOrders);
        stats.put("breachedOrders", breachedOrders);

        stats.put("slaApplicableOrders", slaApplicableOrders);
        stats.put("slaCompliantOrders", slaCompliantOrders);
        stats.put("slaCompliance", slaCompliance);

        stats.put("technicianBreakdown", technicianBreakdown);
        stats.put("siteBreakdown", siteBreakdown);

        return stats;
    }

    // =====================================================
    // TECHNICIAN BREAKDOWN
    // =====================================================

    private List<Map<String, Object>> buildTechnicianBreakdown(
            List<WorkOrder> orders) {

        Map<String, Long> technicianCounts =
                orders.stream()
                        .collect(Collectors.groupingBy(
                                order -> {

                                    User technician =
                                            order.getTechnician();

                                    if (technician == null) {
                                        return "Unassigned";
                                    }

                                    if (technician.getName() == null ||
                                            technician.getName().isBlank()) {
                                        return "Unassigned";
                                    }

                                    return technician.getName();
                                },
                                LinkedHashMap::new,
                                Collectors.counting()
                        ));

        List<Map<String, Object>> result =
                new ArrayList<>();

        technicianCounts.forEach((name, count) -> {

            Map<String, Object> item =
                    new LinkedHashMap<>();

            item.put("name", name);
            item.put("count", count);

            result.add(item);
        });

        return result;
    }

    // =====================================================
    // SITE BREAKDOWN
    // =====================================================

    private List<Map<String, Object>> buildSiteBreakdown(
            List<WorkOrder> orders) {

        Map<String, Long> siteCounts =
                orders.stream()
                        .collect(Collectors.groupingBy(
                                order -> {

                                    if (order.getSite() == null) {
                                        return "Unknown Site";
                                    }

                                    if (order.getSite().getName() == null ||
                                            order.getSite().getName().isBlank()) {
                                        return "Unknown Site";
                                    }

                                    return order.getSite().getName();
                                },
                                LinkedHashMap::new,
                                Collectors.counting()
                        ));

        List<Map<String, Object>> result =
                new ArrayList<>();

        siteCounts.forEach((name, count) -> {

            Map<String, Object> item =
                    new LinkedHashMap<>();

            item.put("name", name);
            item.put("count", count);

            result.add(item);
        });

        return result;
    }

    // =====================================================
    // DISPATCHER DASHBOARD
    // =====================================================

    public Map<String, Long> getDispatcherDashboardStats() {

        Map<String, Long> stats =
                new LinkedHashMap<>();

        long totalOrders =
                workOrderRepository.count();

        long newOrders =
                workOrderRepository
                        .findByStatus(
                                WorkOrderStatus.NEW,
                                org.springframework.data.domain.PageRequest
                                        .of(0, Integer.MAX_VALUE))
                        .getTotalElements();

        long assignedOrders =
                workOrderRepository
                        .findByStatus(
                                WorkOrderStatus.ASSIGNED,
                                org.springframework.data.domain.PageRequest
                                        .of(0, Integer.MAX_VALUE))
                        .getTotalElements();

        long inProgressOrders =
                workOrderRepository
                        .findByStatus(
                                WorkOrderStatus.IN_PROGRESS,
                                org.springframework.data.domain.PageRequest
                                        .of(0, Integer.MAX_VALUE))
                        .getTotalElements();

        long onHoldOrders =
                workOrderRepository
                        .findByStatus(
                                WorkOrderStatus.ON_HOLD,
                                org.springframework.data.domain.PageRequest
                                        .of(0, Integer.MAX_VALUE))
                        .getTotalElements();

        long completedOrders =
                workOrderRepository
                        .findByStatus(
                                WorkOrderStatus.COMPLETED,
                                org.springframework.data.domain.PageRequest
                                        .of(0, Integer.MAX_VALUE))
                        .getTotalElements();

        long cancelledOrders =
                workOrderRepository
                        .findByStatus(
                                WorkOrderStatus.CANCELLED,
                                org.springframework.data.domain.PageRequest
                                        .of(0, Integer.MAX_VALUE))
                        .getTotalElements();

        LocalDateTime now =
                LocalDateTime.now();

        List<WorkOrderStatus> closedStatuses =
                Arrays.asList(
                        WorkOrderStatus.COMPLETED,
                        WorkOrderStatus.CLOSED,
                        WorkOrderStatus.CANCELLED
                );

        long breachedOrders =
                workOrderRepository
                        .findBySlaDueDateBeforeAndStatusNotIn(
                                now,
                                closedStatuses
                        )
                        .size();

        long atRiskOrders =
                workOrderRepository
                        .findBySlaDueDateBetweenAndStatusNotIn(
                                now,
                                now.plusHours(24),
                                closedStatuses
                        )
                        .size();

        stats.put("totalOrders", totalOrders);
        stats.put("newOrders", newOrders);
        stats.put("assignedOrders", assignedOrders);
        stats.put("inProgressOrders", inProgressOrders);
        stats.put("onHoldOrders", onHoldOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("cancelledOrders", cancelledOrders);
        stats.put("breachedOrders", breachedOrders);
        stats.put("atRiskOrders", atRiskOrders);

        return stats;
    }
}