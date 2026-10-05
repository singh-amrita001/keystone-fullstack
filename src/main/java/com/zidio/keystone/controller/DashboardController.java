package com.zidio.keystone.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.dto.ActivityResponse;
import com.zidio.keystone.repository.WorkOrderRepository;
import com.zidio.keystone.service.DashboardService;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final WorkOrderRepository workOrderRepository;
    private final DashboardService dashboardService;

    public DashboardController(
            WorkOrderRepository workOrderRepository,
            DashboardService dashboardService) {

        this.workOrderRepository = workOrderRepository;
        this.dashboardService = dashboardService;
    }

    // =====================================================
    // DASHBOARD STATISTICS + FILTERS
    // =====================================================

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardStats(

            @RequestParam(required = false)
            String status,

            @RequestParam(required = false)
            String priority,

            @RequestParam(required = false)
            Long technicianId,

            @RequestParam(required = false)
            Long siteId) {

        return dashboardService.getDashboardStats(
                status,
                priority,
                technicianId,
                siteId
        );
    }

    // =====================================================
    // DISPATCHER DASHBOARD
    // =====================================================

    @GetMapping("/dashboard/dispatcher")
    public Map<String, Long> getDispatcherDashboardStats() {

        return dashboardService.getDispatcherDashboardStats();
    }

    // =====================================================
    // RECENT ACTIVITY
    // =====================================================

    @GetMapping("/dashboard/activity")
    public List<ActivityResponse> getRecentActivity() {

        List<WorkOrder> orders =
                workOrderRepository.findAll();

        List<ActivityResponse> activities =
                new ArrayList<>();

        orders.stream()
                .limit(5)
                .forEach(order -> {

                    activities.add(
                            new ActivityResponse(
                                    "Work Order "
                                            + order.getWorkOrderCode()
                                            + " created",
                                    order.getCreatedAt()
                            )
                    );
                });

        return activities;
    }
}