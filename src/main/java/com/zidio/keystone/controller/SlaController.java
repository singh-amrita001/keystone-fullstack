package com.zidio.keystone.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.dto.WorkOrderResponse;
import com.zidio.keystone.service.SlaMonitoringService;

@RestController
@RequestMapping("/api/sla")
public class SlaController {

	private final SlaMonitoringService slaMonitoringService;

	public SlaController(SlaMonitoringService slaMonitoringService) {
		this.slaMonitoringService = slaMonitoringService;
	}

	/**
	 * Get all work orders whose SLA is at risk.
	 */
	@GetMapping("/at-risk")
	public ResponseEntity<List<WorkOrderResponse>> getAtRiskWorkOrders() {

		List<WorkOrder> workOrders = slaMonitoringService.findAtRiskWorkOrders();

		List<WorkOrderResponse> response = workOrders.stream().map(WorkOrderResponse::new).toList();

		return ResponseEntity.ok(response);
	}

	/**
	 * Get all work orders whose SLA has been breached.
	 */
	@GetMapping("/breached")
	public ResponseEntity<List<WorkOrderResponse>> getBreachedWorkOrders() {

		List<WorkOrder> workOrders = slaMonitoringService.findBreachedWorkOrders();

		List<WorkOrderResponse> response = workOrders.stream().map(WorkOrderResponse::new).toList();

		return ResponseEntity.ok(response);
	}
}