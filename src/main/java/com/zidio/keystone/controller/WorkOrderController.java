package com.zidio.keystone.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zidio.keystone.domain.WorkOrderStatus;
import com.zidio.keystone.dto.AssignTechnicianRequest;
import com.zidio.keystone.dto.WorkOrderRequest;
import com.zidio.keystone.dto.WorkOrderResponse;
import com.zidio.keystone.dto.WorkOrderUpdateRequest;
import com.zidio.keystone.service.WorkOrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workorders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    // =========================================================
    // CREATE WORK ORDER
    // =========================================================

    @PostMapping
    public WorkOrderResponse createWorkOrder(
            @Valid @RequestBody WorkOrderRequest request) {

        return workOrderService.createWorkOrder(request);
    }

    // =========================================================
    // F9 - CUSTOMER CREATE REQUEST
    // =========================================================

    @PostMapping("/customer-request")
    public ResponseEntity<WorkOrderResponse> createCustomerRequest(
            @Valid @RequestBody WorkOrderRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workOrderService.createCustomerRequest(request));
    }

    // =========================================================
    // GET ALL WORK ORDERS
    // =========================================================

    @GetMapping
    public Page<?> getAllWorkOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ALL") String status) {

        return workOrderService.getAllWorkOrders(
                page,
                size,
                sortBy,
                status);
    }

    // =========================================================
    // F9 - WORK ORDER HISTORY
    // =========================================================

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getWorkOrderHistory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                workOrderService.getWorkOrderHistory(id));
    }

    // =========================================================
    // GET WORK ORDER BY ID
    // =========================================================

    @GetMapping("/{id}")
    public Object getWorkOrderById(
            @PathVariable Long id) {

        return workOrderService.getWorkOrderById(id);
    }

    // =========================================================
    // UPDATE WORK ORDER
    // =========================================================

    @PutMapping("/{id}")
    public WorkOrderResponse updateWorkOrder(
            @PathVariable Long id,
            @RequestBody WorkOrderUpdateRequest request) {

        return workOrderService.updateWorkOrder(
                id,
                request);
    }

    // =========================================================
    // UPDATE WORK ORDER STATUS
    // =========================================================

    @PutMapping("/{id}/status")
    public Object updateStatus(
            @PathVariable Long id,
            @RequestParam WorkOrderStatus status) {

        return workOrderService.updateStatus(
                id,
                status);
    }

    // =========================================================
    // ASSIGN TECHNICIAN
    // =========================================================

    @PutMapping("/{id}/technician")
    public WorkOrderResponse assignTechnician(
            @PathVariable Long id,
            @RequestBody AssignTechnicianRequest request) {

        return workOrderService.assignTechnician(
                id,
                request.getTechnicianId());
    }

    // =========================================================
    // DELETE WORK ORDER
    // =========================================================

    @DeleteMapping("/{id}")
    public String deleteWorkOrder(
            @PathVariable Long id) {

        workOrderService.deleteWorkOrder(id);

        return "Work Order deleted successfully";
    }
}