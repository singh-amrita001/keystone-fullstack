package com.zidio.keystone.service;
import com.zidio.keystone.dto.CustomerWorkOrderResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zidio.keystone.customexception.ResourceNotFoundException;
import com.zidio.keystone.customexception.WorkOrderConflictException;
import com.zidio.keystone.domain.Customer;
import com.zidio.keystone.domain.Site;
import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.domain.WorkOrderStatus;
import com.zidio.keystone.domain.WorkOrderStatusHistory;
import com.zidio.keystone.dto.CustomerHistoryResponse;
import com.zidio.keystone.dto.WorkOrderRequest;
import com.zidio.keystone.dto.WorkOrderResponse;
import com.zidio.keystone.dto.WorkOrderUpdateRequest;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.CustomerRepository;
import com.zidio.keystone.repository.PartUsageRepository;
import com.zidio.keystone.repository.SiteRepository;
import com.zidio.keystone.repository.TimeLogRepository;
import com.zidio.keystone.repository.UserRepository;
import com.zidio.keystone.repository.WorkOrderRepository;
import com.zidio.keystone.repository.WorkOrderStatusHistoryRepository;

@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;

    private final WorkOrderStatusHistoryRepository workOrderStatusHistoryRepository;

    private final UserRepository userRepository;

    private final CustomerRepository customerRepository;

    private final SiteRepository siteRepository;

    private final NotificationService notificationService;

    // F6
    private final PartUsageRepository partUsageRepository;

    private final TimeLogRepository timeLogRepository;

    public WorkOrderService(
            WorkOrderRepository workOrderRepository,
            WorkOrderStatusHistoryRepository workOrderStatusHistoryRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            SiteRepository siteRepository,
            NotificationService notificationService,
            PartUsageRepository partUsageRepository,
            TimeLogRepository timeLogRepository) {

        this.workOrderRepository = workOrderRepository;
        this.workOrderStatusHistoryRepository =
                workOrderStatusHistoryRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.siteRepository = siteRepository;
        this.notificationService = notificationService;

        // F6
        this.partUsageRepository = partUsageRepository;
        this.timeLogRepository = timeLogRepository;
    }

    // =========================================================
    // CREATE WORK ORDER - INTERNAL USERS
    // =========================================================

    @Transactional
    public WorkOrderResponse createWorkOrder(
            WorkOrderRequest request) {

        User currentUser = getAuthenticatedUser();

        // CUSTOMER must use the customer-specific endpoint.
        if ("CUSTOMER".equalsIgnoreCase(currentUser.getRole())) {
            throw new AccessDeniedException(
                    "Customers must use the customer request endpoint.");
        }

        validatePriority(request.getPriority());

        Customer customer =
                customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with id: "
                                                + request.getCustomerId()));

        Site site =
                siteRepository.findById(request.getSiteId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Site not found with id: "
                                                + request.getSiteId()));

        // Validate site belongs to customer
        if (site.getCustomer() == null
                || !site.getCustomer()
                        .getId()
                        .equals(customer.getId())) {

            throw new IllegalArgumentException(
                    "Selected site does not belong to the selected customer.");
        }

        WorkOrder workOrder = new WorkOrder();

        // Server generates unique work order code
        workOrder.setWorkOrderCode(
                generateUniqueWorkOrderCode());

        workOrder.setTitle(
                request.getTitle().trim());

        workOrder.setDescription(
                request.getDescription().trim());

        workOrder.setPriority(
                request.getPriority()
                        .trim()
                        .toUpperCase());

        // New work orders always start with NEW
        workOrder.setStatus(
                WorkOrderStatus.NEW);

        workOrder.setCustomer(customer);
        workOrder.setSite(site);

        // Calculate SLA from priority
        workOrder.setSlaDueDate(
                calculateSlaDueDate(
                        request.getPriority()));

        WorkOrder saved =
                workOrderRepository.save(workOrder);

        return buildWorkOrderResponse(saved);
    }

    // =========================================================
    // F9 - CUSTOMER RAISE REQUEST
    // =========================================================

    @Transactional
    public WorkOrderResponse createCustomerRequest(
            WorkOrderRequest request) {

        User currentUser = getAuthenticatedUser();

        // Only CUSTOMER accounts can use this method
        if (currentUser.getRole() == null
                || !currentUser.getRole()
                        .equalsIgnoreCase("CUSTOMER")) {

            throw new AccessDeniedException(
                    "Only customers can create customer requests.");
        }

        // Customer account must be linked to a customer organization
        if (currentUser.getCustomer() == null
                || currentUser.getCustomer().getId() == null) {

            throw new AccessDeniedException(
                    "Customer account is not linked to a customer organization.");
        }

        validatePriority(request.getPriority());

        // Customer is always taken from logged-in user
        Customer customer =
                currentUser.getCustomer();

        // Find requested site
        Site site =
                siteRepository.findById(request.getSiteId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Site not found with id: "
                                                + request.getSiteId()));

        // =====================================================
        // SECURITY:
        // Site MUST belong to logged-in customer's organization
        // =====================================================

        if (site.getCustomer() == null
                || site.getCustomer().getId() == null
                || !site.getCustomer()
                        .getId()
                        .equals(customer.getId())) {

            throw new AccessDeniedException(
                    "You can only create requests for your own sites.");
        }

        // =====================================================
        // CREATE NORMAL WORK ORDER
        // =====================================================

        WorkOrder workOrder = new WorkOrder();

        workOrder.setWorkOrderCode(
                generateUniqueWorkOrderCode());

        if (request.getTitle() == null
                || request.getTitle().isBlank()) {

            throw new IllegalArgumentException(
                    "Title is required.");
        }

        if (request.getDescription() == null
                || request.getDescription().isBlank()) {

            throw new IllegalArgumentException(
                    "Description is required.");
        }

        workOrder.setTitle(
                request.getTitle().trim());

        workOrder.setDescription(
                request.getDescription().trim());

        workOrder.setPriority(
                request.getPriority()
                        .trim()
                        .toUpperCase());

        // Customer requests enter normal pipeline as NEW
        workOrder.setStatus(
                WorkOrderStatus.NEW);

        // Customer is always taken from logged-in account
        workOrder.setCustomer(customer);
        workOrder.setSite(site);

        // SLA calculated exactly like normal work orders
        workOrder.setSlaDueDate(
                calculateSlaDueDate(
                        request.getPriority()));

        WorkOrder saved =
                workOrderRepository.save(workOrder);

        return buildWorkOrderResponse(saved);
    }

    // =========================================================
    // GET AUTHENTICATED USER
    // =========================================================

    private User getAuthenticatedUser() {

        String currentUserEmail =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found."));
    }

    // =========================================================
    // GENERATE UNIQUE WORK ORDER CODE
    // =========================================================

    private String generateUniqueWorkOrderCode() {

        String code;

        do {
            String date =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyyMMdd"));

            String random =
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 6)
                            .toUpperCase();

            code =
                    "WO-" + date + "-" + random;

        } while (
                workOrderRepository
                        .existsByWorkOrderCode(code));

        return code;
    }

    // =========================================================
    // VALIDATE PRIORITY
    // =========================================================

    private void validatePriority(
            String priority) {

        if (priority == null
                || priority.isBlank()) {

            throw new IllegalArgumentException(
                    "Priority is required.");
        }

        String normalized =
                priority.trim().toUpperCase();

        switch (normalized) {

            case "CRITICAL":
            case "HIGH":
            case "MEDIUM":
            case "LOW":
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid priority: "
                                + priority
                                + ". Allowed values: "
                                + "CRITICAL, HIGH, MEDIUM, LOW");
        }
    }

    // =========================================================
    // CALCULATE SLA DUE DATE
    // =========================================================

    private LocalDateTime calculateSlaDueDate(
            String priority) {

        LocalDateTime now =
                LocalDateTime.now();

        return switch (
                priority.trim().toUpperCase()) {

            case "CRITICAL" ->
                    now.plusHours(4);

            case "HIGH" ->
                    now.plusHours(8);

            case "MEDIUM" ->
                    now.plusHours(24);

            case "LOW" ->
                    now.plusHours(48);

            default ->
                    now.plusHours(24);
        };
    }

    // =========================================================
    // GET ALL WORK ORDERS
    // =========================================================

    @Transactional(readOnly = true)
    public Page<?> getAllWorkOrders(
            int page,
            int size,
            String sortBy,
            String status) {

        PageRequest pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(sortBy));

        User currentUser =
                getAuthenticatedUser();

        String role =
                currentUser.getRole();

        if (role == null
                || role.isBlank()) {

            throw new AccessDeniedException(
                    "User role is missing.");
        }

        role = role.toUpperCase();

        Page<WorkOrder> workOrders;

        // =====================================================
        // TECHNICIAN
        // =====================================================

        if (role.equals("TECHNICIAN")) {

            if (status == null
                    || status.isBlank()
                    || status.equalsIgnoreCase("ALL")) {

                workOrders =
                        workOrderRepository
                                .findByTechnicianId(
                                        currentUser.getId(),
                                        pageable);

            } else {

                workOrders =
                        workOrderRepository
                                .findByTechnicianIdAndStatus(
                                        currentUser.getId(),
                                        convertStatus(status),
                                        pageable);
            }
        }

        // =====================================================
        // CUSTOMER
        // =====================================================

        else if (role.equals("CUSTOMER")) {

            if (currentUser.getCustomer() == null
                    || currentUser.getCustomer().getId() == null) {

                throw new AccessDeniedException(
                        "Customer account is not linked to a customer organization.");
            }

            Long customerId =
                    currentUser.getCustomer().getId();

            if (status == null
                    || status.isBlank()
                    || status.equalsIgnoreCase("ALL")) {

                workOrders =
                        workOrderRepository.findByCustomerId(
                                customerId,
                                pageable);

            } else {

                workOrders =
                        workOrderRepository
                                .findByCustomerIdAndStatus(
                                        customerId,
                                        convertStatus(status),
                                        pageable);
            }
        }

        // =====================================================
        // ADMIN / DISPATCHER / MANAGER
        // =====================================================

        else {

            if (status == null
                    || status.isBlank()
                    || status.equalsIgnoreCase("ALL")) {

                workOrders =
                        workOrderRepository
                                .findAll(pageable);

            } else {

                workOrders =
                        workOrderRepository
                                .findByStatus(
                                        convertStatus(status),
                                        pageable);
            }
        }

        if (role.equals("CUSTOMER")) {

            return workOrders.map(
                    this::buildCustomerWorkOrderResponse);
        }

        return workOrders.map(
                this::buildWorkOrderResponse);
    }

    // =========================================================
    // GET WORK ORDER BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public Object getWorkOrderById(Long id) {

        WorkOrder workOrder =
                workOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work Order not found with id: "
                                                + id));

        User currentUser = getAuthenticatedUser();

        // =====================================================
        // CUSTOMER SECURITY
        // =====================================================

        if (currentUser.getRole() != null
                && currentUser.getRole()
                        .equalsIgnoreCase("CUSTOMER")) {

            if (currentUser.getCustomer() == null
                    || currentUser.getCustomer().getId() == null) {

                throw new AccessDeniedException(
                        "Customer account is not linked to a customer organization.");
            }

            if (workOrder.getCustomer() == null
                    || workOrder.getCustomer().getId() == null
                    || !workOrder.getCustomer()
                            .getId()
                            .equals(
                                    currentUser.getCustomer().getId())) {

                throw new AccessDeniedException(
                        "You are not authorized to view this work order.");
            }
        }

        return buildWorkOrderResponse(workOrder);
    }

    // =========================================================
    // F9 - CUSTOMER VIEW WORK ORDER HISTORY
    // =========================================================

    @Transactional(readOnly = true)
    public java.util.List<CustomerHistoryResponse> getWorkOrderHistory(
            Long workOrderId) {

        // =====================================================
        // FIND WORK ORDER
        // =====================================================

        WorkOrder workOrder =
                workOrderRepository.findById(workOrderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work Order not found with id: "
                                                + workOrderId));

        // =====================================================
        // GET LOGGED-IN USER
        // =====================================================

        User currentUser =
                getAuthenticatedUser();

        // =====================================================
        // CUSTOMER SECURITY
        // =====================================================

        if (currentUser.getRole() != null
                && currentUser.getRole()
                        .equalsIgnoreCase("CUSTOMER")) {

            if (currentUser.getCustomer() == null
                    || currentUser.getCustomer().getId() == null) {

                throw new AccessDeniedException(
                        "Customer account is not linked to a customer organization.");
            }

            if (workOrder.getCustomer() == null
                    || workOrder.getCustomer().getId() == null
                    || !workOrder.getCustomer()
                            .getId()
                            .equals(
                                    currentUser.getCustomer().getId())) {

                throw new AccessDeniedException(
                        "You are not authorized to view this work order history.");
            }
        }

        // =====================================================
        // GET HISTORY
        // =====================================================

        java.util.List<WorkOrderStatusHistory> historyList =
                workOrderStatusHistoryRepository
                        .findByWorkOrderIdOrderByChangedAtDesc(
                                workOrderId);

        // =====================================================
        // CUSTOMER-SAFE RESPONSE
        // =====================================================
        // Only expose:
        // - fromStatus
        // - toStatus
        // - changedAt
        //
        // Do NOT expose:
        // - changedBy
        // - internal note
        // - complete WorkOrder object
        // =====================================================

        return historyList.stream()
                .map(history ->
                        new CustomerHistoryResponse(
                                history.getFromStatus(),
                                history.getToStatus(),
                                history.getChangedAt()))
                .toList();
    }

    // =========================================================
    // UPDATE WORK ORDER
    // =========================================================

    @Transactional
    public WorkOrderResponse updateWorkOrder(
            Long id,
            WorkOrderUpdateRequest request) {

        User currentUser =
                getAuthenticatedUser();

        // CUSTOMER cannot edit work orders
        if ("CUSTOMER".equalsIgnoreCase(
                currentUser.getRole())) {

            throw new AccessDeniedException(
                    "Customers cannot edit work orders.");
        }

        WorkOrder existing =
                workOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work Order not found with id: "
                                                + id));

        // Closed/cancelled work orders cannot be edited
        if (existing.getStatus() == WorkOrderStatus.CLOSED
                || existing.getStatus()
                        == WorkOrderStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Closed or cancelled work orders cannot be edited.");
        }

        // =====================================================
        // TITLE
        // =====================================================

        if (request.getTitle() != null) {

            if (request.getTitle().isBlank()) {

                throw new IllegalArgumentException(
                        "Title cannot be blank.");
            }

            existing.setTitle(
                    request.getTitle().trim());
        }

        // =====================================================
        // DESCRIPTION
        // =====================================================

        if (request.getDescription() != null) {

            if (request.getDescription().isBlank()) {

                throw new IllegalArgumentException(
                        "Description cannot be blank.");
            }

            existing.setDescription(
                    request.getDescription().trim());
        }

        // =====================================================
        // PRIORITY
        // =====================================================

        if (request.getPriority() != null) {

            validatePriority(
                    request.getPriority());

            existing.setPriority(
                    request.getPriority()
                            .trim()
                            .toUpperCase());

            // Recalculate SLA
            existing.setSlaDueDate(
                    calculateSlaDueDate(
                            request.getPriority()));
        }

        /*
         * Status is intentionally NOT updated here.
         *
         * Status changes must go through updateStatus()
         * so lifecycle validation and status history
         * are always applied.
         */

        WorkOrder updated =
                workOrderRepository.save(existing);

        return buildWorkOrderResponse(updated);
    }

    // =========================================================
    // DELETE WORK ORDER
    // =========================================================

    @Transactional
    public void deleteWorkOrder(
            Long id) {

        User currentUser =
                getAuthenticatedUser();

        // CUSTOMER cannot delete work orders
        if ("CUSTOMER".equalsIgnoreCase(
                currentUser.getRole())) {

            throw new AccessDeniedException(
                    "Customers cannot delete work orders.");
        }

        WorkOrder workOrder =
                workOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work Order not found with id: "
                                                + id));

        if (workOrder.getStatus() == WorkOrderStatus.CLOSED
                || workOrder.getStatus()
                        == WorkOrderStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Closed or cancelled work orders cannot be deleted.");
        }

        workOrderRepository.delete(workOrder);
    }

    // =========================================================
    // ASSIGN / REASSIGN TECHNICIAN
    // =========================================================

    @Transactional
    public WorkOrderResponse assignTechnician(
            Long workOrderId,
            Long technicianId) {

        User currentUser =
                getAuthenticatedUser();

        // CUSTOMER cannot assign technicians
        if ("CUSTOMER".equalsIgnoreCase(
                currentUser.getRole())) {

            throw new AccessDeniedException(
                    "Customers cannot assign technicians.");
        }

        WorkOrder workOrder =
                workOrderRepository.findById(workOrderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work Order not found with id: "
                                                + workOrderId));

        // Terminal state protection
        if (workOrder.getStatus() == WorkOrderStatus.CLOSED
                || workOrder.getStatus()
                        == WorkOrderStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Closed or cancelled work orders cannot be assigned.");
        }

        // =====================================================
        // FIND TECHNICIAN
        // =====================================================

        User technician =
                userRepository.findById(technicianId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Technician not found with id: "
                                                + technicianId));

        // =====================================================
        // VALIDATE TECHNICIAN
        // =====================================================

        if (technician.getRole() == null
                || !technician.getRole()
                        .equalsIgnoreCase("TECHNICIAN")) {

            throw new IllegalArgumentException(
                    "Selected user is not a TECHNICIAN.");
        }

        // =====================================================
        // ASSIGN TECHNICIAN
        // =====================================================

        workOrder.setTechnician(technician);

        // =====================================================
        // NEW -> ASSIGNED
        // =====================================================

        if (workOrder.getStatus()
                == WorkOrderStatus.NEW) {

            WorkOrderStatus oldStatus =
                    workOrder.getStatus();

            workOrder.setStatus(
                    WorkOrderStatus.ASSIGNED);

            WorkOrder updated =
                    workOrderRepository.save(workOrder);

            // Status history
            WorkOrderStatusHistory history =
                    new WorkOrderStatusHistory();

            history.setWorkOrder(updated);
            history.setFromStatus(oldStatus);
            history.setToStatus(
                    WorkOrderStatus.ASSIGNED);

            history.setChangedBy("SYSTEM");

            history.setChangedAt(
                    LocalDateTime.now());

            history.setNote(
                    "Technician "
                            + technician.getName()
                            + " assigned to work order.");

            workOrderStatusHistoryRepository.saveAndFlush(history);

            // Notification
            notificationService.createNotification(
                    technician,
                    updated.getId(),
                    "WORK_ORDER_ASSIGNED",
                    "Work order "
                            + updated.getWorkOrderCode()
                            + " has been assigned to you.");

            return buildWorkOrderResponse(updated);
        }

        // =====================================================
        // REASSIGN EXISTING WORK ORDER
        // =====================================================

   

     WorkOrder updated =
             workOrderRepository.save(workOrder);

     notificationService.createNotification(
             technician,
             updated.getId(),
             "WORK_ORDER_ASSIGNED",
             "Work order "
                     + updated.getWorkOrderCode()
                     + " has been assigned to you.");

     return buildWorkOrderResponse(updated);
      
    }
    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @Transactional
    public WorkOrder updateStatus(
            Long id,
            WorkOrderStatus newStatus) {

        WorkOrder workOrder =
                workOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work Order not found with id: "
                                                + id));

        WorkOrderStatus currentStatus =
                workOrder.getStatus();

        validateTransition(
                currentStatus,
                newStatus);

        User currentUser =
                getAuthenticatedUser();

        String role =
                currentUser.getRole();

        if (role == null || role.isBlank()) {

            throw new IllegalStateException(
                    "User role is missing.");
        }

        role = role.toUpperCase();

        // =====================================================
        // ROLE-BASED AUTHORIZATION
        // =====================================================

        if (role.equals("TECHNICIAN")) {

            if (workOrder.getTechnician() == null
                    || !workOrder.getTechnician()
                            .getId()
                            .equals(currentUser.getId())) {

                throw new AccessDeniedException(
                        "You can only update work orders assigned to you.");
            }

            boolean technicianAllowed =
                    newStatus == WorkOrderStatus.IN_PROGRESS
                    || newStatus == WorkOrderStatus.ON_HOLD
                    || newStatus == WorkOrderStatus.COMPLETED;

            if (!technicianAllowed) {

                throw new AccessDeniedException(
                        "Technicians cannot change the work order to "
                                + newStatus);
            }

        } else if (role.equals("DISPATCHER")) {

            boolean dispatcherAllowed =
                    newStatus == WorkOrderStatus.ASSIGNED
                    || newStatus == WorkOrderStatus.CANCELLED;

            if (!dispatcherAllowed) {

                throw new AccessDeniedException(
                        "Dispatchers cannot change the work order to "
                                + newStatus);
            }

        } else if (role.equals("CUSTOMER")) {

            throw new AccessDeniedException(
                    "Customers cannot update work order status.");

        } else if (!role.equals("ADMIN")
                && !role.equals("MANAGER")) {

            throw new AccessDeniedException(
                    "You are not authorized to update work order status.");
        }

        // =====================================================
        // SAVE STATUS
        // =====================================================

        workOrder.setStatus(newStatus);

        WorkOrder updated =
                workOrderRepository.save(workOrder);

        // =====================================================
        // SAVE STATUS HISTORY
        // =====================================================

        WorkOrderStatusHistory history =
                new WorkOrderStatusHistory();

        history.setWorkOrder(updated);

        history.setFromStatus(currentStatus);

        history.setToStatus(newStatus);

        history.setChangedBy(
                currentUser.getEmail());

        history.setChangedAt(
                LocalDateTime.now());

        history.setNote(
                "Work order status changed from "
                        + currentStatus
                        + " to "
                        + newStatus);

        // Force INSERT immediately
        workOrderStatusHistoryRepository
                .saveAndFlush(history);

        return updated;
    }

    // =========================================================
    // STATUS TRANSITION VALIDATION
    // =========================================================

    private void validateTransition(
            WorkOrderStatus currentStatus,
            WorkOrderStatus newStatus) {

        if (currentStatus == null) {

            throw new IllegalStateException(
                    "Current work order status is missing.");
        }

        if (newStatus == null) {

            throw new IllegalArgumentException(
                    "New work order status is required.");
        }

        boolean allowed =
                switch (currentStatus) {

                    case NEW ->
                            newStatus == WorkOrderStatus.ASSIGNED
                            || newStatus == WorkOrderStatus.CANCELLED;

                    case ASSIGNED ->
                            newStatus == WorkOrderStatus.IN_PROGRESS
                            || newStatus == WorkOrderStatus.CANCELLED;

                    case IN_PROGRESS ->
                            newStatus == WorkOrderStatus.ON_HOLD
                            || newStatus == WorkOrderStatus.COMPLETED;

                    case ON_HOLD ->
                            newStatus == WorkOrderStatus.IN_PROGRESS;

                    case COMPLETED ->
                            newStatus == WorkOrderStatus.CLOSED;

                    case CLOSED, CANCELLED ->
                            false;
                };

                if (!allowed) {

                    throw new WorkOrderConflictException(
                            "Invalid work order status transition: "
                                   + currentStatus
                                    + " -> "
                                    + newStatus);
                }
    }

    // =========================================================
    // STATUS CONVERTER
    // =========================================================

    private WorkOrderStatus convertStatus(
            String status) {

        try {

            return WorkOrderStatus.valueOf(
                    status.trim().toUpperCase());

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "Invalid work order status: "
                            + status
                            + ". Allowed values: "
                            + "NEW, ASSIGNED, IN_PROGRESS, "
                            + "ON_HOLD, COMPLETED, CLOSED, CANCELLED");
        }
    }

    // =========================================================
    // BUILD WORK ORDER RESPONSE WITH F6 TOTALS
    // =========================================================

    private WorkOrderResponse buildWorkOrderResponse(
            WorkOrder workOrder) {

        WorkOrderResponse response =
                new WorkOrderResponse(workOrder);

        // =====================================================
        // TOTAL PARTS COST
        // =====================================================

        BigDecimal partsCost =
                partUsageRepository
                        .getTotalPartsCostByWorkOrderId(
                                workOrder.getId());

        if (partsCost == null) {
            partsCost = BigDecimal.ZERO;
        }

        // =====================================================
        // TOTAL LABOUR MINUTES
        // =====================================================

        Integer labourMinutes =
                timeLogRepository
                        .getTotalMinutesByWorkOrderId(
                                workOrder.getId());

        if (labourMinutes == null) {
            labourMinutes = 0;
        }

        // =====================================================
        // SET F6 TOTALS
        // =====================================================

        response.setPartsCost(partsCost);
        response.setLabourMinutes(labourMinutes);

        return response;
    }
    private CustomerWorkOrderResponse buildCustomerWorkOrderResponse(
            WorkOrder workOrder) {

        CustomerWorkOrderResponse response =
                new CustomerWorkOrderResponse();

        response.setId(workOrder.getId());
        response.setWorkOrderCode(workOrder.getWorkOrderCode());
        response.setTitle(workOrder.getTitle());
        response.setDescription(workOrder.getDescription());
        response.setPriority(workOrder.getPriority());

        if (workOrder.getStatus() != null) {
            response.setStatus(
                    workOrder.getStatus().name());
        }

        if (workOrder.getCustomer() != null) {
            response.setCustomerId(
                    workOrder.getCustomer().getId());

            response.setCustomerName(
                    workOrder.getCustomer().getName());
        }

        if (workOrder.getSite() != null) {
            response.setSiteId(
                    workOrder.getSite().getId());

            response.setSiteName(
                    workOrder.getSite().getName());
        }

        response.setCreatedAt(
                workOrder.getCreatedAt());

        response.setSlaDueDate(
                workOrder.getSlaDueDate());

        // Calculate SLA status using the existing WorkOrderResponse
        WorkOrderResponse internalResponse =
                buildWorkOrderResponse(workOrder);

        if (internalResponse.getSlaStatus() != null) {
            response.setSlaStatus(
                    internalResponse.getSlaStatus().name());
        }

        response.setPartsCost(
                internalResponse.getPartsCost());

        response.setLabourMinutes(
                internalResponse.getLabourMinutes());

        return response;
    }
}