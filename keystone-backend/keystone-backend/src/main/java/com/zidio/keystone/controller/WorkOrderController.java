package com.zidio.keystone.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.zidio.keystone.dto.WorkOrderRequest;
import com.zidio.keystone.dto.WorkOrderResponse;
import com.zidio.keystone.dto.WorkOrderUpdateRequest;

import com.zidio.keystone.service.WorkOrderService;
import com.zidio.keystone.dto.WorkOrderUpdateRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/workorders")
public class WorkOrderController {


    private final WorkOrderService workOrderService;


    public WorkOrderController(
            WorkOrderService workOrderService) {

        this.workOrderService = workOrderService;
    }



    // ============================
    // CREATE WORK ORDER
    // ============================

    @PostMapping
    public WorkOrderResponse createWorkOrder(
            @Valid @RequestBody WorkOrderRequest request) {


        return workOrderService.createWorkOrder(request);
    }





    // ============================
    // GET ALL WORK ORDERS
    // ============================

    @GetMapping
    public Page<WorkOrderResponse> getAllWorkOrders(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "ALL")
            String status

    ) {


        return workOrderService.getAllWorkOrders(
                page,
                size,
                sortBy,
                status
        );

    }





    // ============================
    // GET WORK ORDER BY ID
    // ============================

    @GetMapping("/{id}")
    public WorkOrderResponse getWorkOrderById(

            @PathVariable Long id

    ) {


        return workOrderService.getWorkOrderById(id);

    }





    // ============================
    // UPDATE WORK ORDER
    // Partial Update Supported
    // ============================

    @PutMapping("/{id}")
    public WorkOrderResponse updateWorkOrder(

            @PathVariable Long id,

            @RequestBody WorkOrderUpdateRequest request

    ) {


        return workOrderService.updateWorkOrder(
                id,
                request
        );

    }





    // ============================
    // DELETE WORK ORDER
    // ============================

    @DeleteMapping("/{id}")
    public String deleteWorkOrder(

            @PathVariable Long id

    ) {


        workOrderService.deleteWorkOrder(id);


        return "Work Order deleted successfully";

    }


}