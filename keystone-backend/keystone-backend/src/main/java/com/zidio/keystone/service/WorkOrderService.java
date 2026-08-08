package com.zidio.keystone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.zidio.keystone.dto.WorkOrderUpdateRequest;
import com.zidio.keystone.customexception.ResourceNotFoundException;
import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.dto.WorkOrderRequest;
import com.zidio.keystone.dto.WorkOrderResponse;
import com.zidio.keystone.repository.WorkOrderRepository;


@Service
public class WorkOrderService {


    private final WorkOrderRepository workOrderRepository;


    public WorkOrderService(
            WorkOrderRepository workOrderRepository) {

        this.workOrderRepository = workOrderRepository;
    }


    // CREATE
    public WorkOrderResponse createWorkOrder(
            WorkOrderRequest request) {


        WorkOrder workOrder = new WorkOrder();

        workOrder.setWorkOrderCode(
                request.getWorkOrderCode()
        );

        workOrder.setTitle(
                request.getTitle()
        );

        workOrder.setDescription(
                request.getDescription()
        );

        workOrder.setPriority(
                request.getPriority()
        );

        workOrder.setStatus(
                request.getStatus()
        );

        workOrder.setSlaDueDate(
                request.getSlaDueDate()
        );


        WorkOrder saved =
                workOrderRepository.save(workOrder);


        return new WorkOrderResponse(saved);
    }



    // GET ALL
 // GET ALL
    public Page<WorkOrderResponse> getAllWorkOrders(
            int page,
            int size,
            String sortBy,
            String status) {

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy)
        );

        Page<WorkOrder> workOrders;

        if (status == null || status.equalsIgnoreCase("ALL")) {

            workOrders = workOrderRepository.findAll(pageable);

        } else {

            workOrders = workOrderRepository.findByStatus(
                    status,
                    pageable
            );
        }

        return workOrders.map(WorkOrderResponse::new);
    }



    // GET BY ID
    public WorkOrderResponse getWorkOrderById(Long id) {


        WorkOrder workOrder =
                workOrderRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Work Order not found with id: " + id
                    )
                );


        return new WorkOrderResponse(workOrder);
    }



    // UPDATE
 // UPDATE
    public WorkOrderResponse updateWorkOrder(
            Long id,
            WorkOrderUpdateRequest request) {


        WorkOrder existing =
                workOrderRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Work Order not found with id: " + id
                    )
                );


        if(request.getWorkOrderCode() != null) {

            existing.setWorkOrderCode(
                    request.getWorkOrderCode()
            );

        }


        if(request.getTitle() != null) {

            existing.setTitle(
                    request.getTitle()
            );

        }


        if(request.getDescription() != null) {

            existing.setDescription(
                    request.getDescription()
            );

        }


        if(request.getPriority() != null) {

            existing.setPriority(
                    request.getPriority()
            );

        }


        if(request.getStatus() != null) {

            existing.setStatus(
                    request.getStatus()
            );

        }


        if(request.getSlaDueDate() != null) {

            existing.setSlaDueDate(
                    request.getSlaDueDate()
            );

        }



        WorkOrder updated =
                workOrderRepository.save(existing);


        return new WorkOrderResponse(updated);

    }

    // DELETE
    public void deleteWorkOrder(Long id) {


        WorkOrder workOrder =
                workOrderRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Work Order not found with id: " + id
                    )
                );


        workOrderRepository.delete(workOrder);
    }
} 