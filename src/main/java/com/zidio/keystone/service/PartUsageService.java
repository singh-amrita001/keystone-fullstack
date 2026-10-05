package com.zidio.keystone.service;

import com.zidio.keystone.domain.Part;
import com.zidio.keystone.domain.PartUsage;
import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.dto.PartUsageRequest;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.PartRepository;
import com.zidio.keystone.repository.PartUsageRepository;
import com.zidio.keystone.repository.UserRepository;
import com.zidio.keystone.repository.WorkOrderRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PartUsageService {

    private final PartUsageRepository partUsageRepository;
    private final PartRepository partRepository;
    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;

    public PartUsageService(
            PartUsageRepository partUsageRepository,
            PartRepository partRepository,
            WorkOrderRepository workOrderRepository,
            UserRepository userRepository) {

        this.partUsageRepository = partUsageRepository;
        this.partRepository = partRepository;
        this.workOrderRepository = workOrderRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"));
    }

    // =========================================================
    // CHECK TECHNICIAN ACCESS TO WORK ORDER
    // =========================================================

    private void validateTechnicianWorkOrderAccess(
            WorkOrder workOrder,
            User currentUser) {

        String role = currentUser.getRole();

        if (role == null ||
                !role.equalsIgnoreCase("TECHNICIAN")) {

            throw new AccessDeniedException(
                    "Only technicians can manage part usage.");
        }

        if (workOrder.getTechnician() == null ||
                !workOrder.getTechnician()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You can only use parts for work orders assigned to you.");
        }
    }

    // =========================================================
    // CREATE PART USAGE
    // =========================================================

    @Transactional
    public PartUsage createPartUsage(
            PartUsageRequest request) {

        if (request.getQuantity() == null ||
                request.getQuantity() < 1) {

            throw new IllegalArgumentException(
                    "Quantity must be at least 1.");
        }

        // Get authenticated technician
        User currentUser = getCurrentUser();

        // Find Part
        Part part = partRepository
                .findById(request.getPartId())
                .orElseThrow(() ->
                        new RuntimeException("Part not found"));

        // Find Work Order
        WorkOrder workOrder = workOrderRepository
                .findById(request.getWorkOrderId())
                .orElseThrow(() ->
                        new RuntimeException("Work order not found"));

        // Technician can use parts only on assigned work orders
        validateTechnicianWorkOrderAccess(
                workOrder,
                currentUser
        );

        // Check stock
        Integer currentStock = part.getStockQuantity();

        if (currentStock == null) {
            currentStock = 0;
        }

        if (request.getQuantity() > currentStock) {

            throw new IllegalArgumentException(
                    "Insufficient stock. Available stock: "
                            + currentStock
                            + ", requested quantity: "
                            + request.getQuantity());
        }

        // Use actual Part unit cost
        BigDecimal unitCost = part.getUnitCost();

        if (unitCost == null) {
            unitCost = BigDecimal.ZERO;
        }

        // Create PartUsage
        PartUsage partUsage = new PartUsage();

        partUsage.setPart(part);
        partUsage.setWorkOrder(workOrder);
        partUsage.setQuantity(request.getQuantity());
        partUsage.setUnitCost(unitCost);

        // Calculate total cost
        BigDecimal totalCost = unitCost.multiply(
                BigDecimal.valueOf(request.getQuantity())
        );

        partUsage.setTotalCost(totalCost);

        // Decrease stock
        part.setStockQuantity(
                currentStock - request.getQuantity()
        );

        // Save Part
        partRepository.save(part);

        // Save PartUsage
        return partUsageRepository.save(partUsage);
    }

    // =========================================================
    // GET PART USAGE BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public PartUsage getPartUsageById(Long id) {

        return partUsageRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Part usage not found"));
    }

    // =========================================================
    // GET ALL PART USAGES - PAGINATION
    // =========================================================

    @Transactional(readOnly = true)
    public Page<PartUsage> getAllPartUsages(
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return partUsageRepository.findAll(pageable);
    }

    // =========================================================
    // GET PART USAGES BY WORK ORDER
    // =========================================================

    @Transactional(readOnly = true)
    public List<PartUsage> getByWorkOrderId(
            Long workOrderId) {

        return partUsageRepository
                .findByWorkOrderId(workOrderId);
    }

    // =========================================================
    // GET PART USAGES BY PART
    // =========================================================

    @Transactional(readOnly = true)
    public List<PartUsage> getByPartId(
            Long partId) {

        return partUsageRepository
                .findByPartId(partId);
    }

    // =========================================================
    // UPDATE PART USAGE
    // =========================================================

    @Transactional
    public PartUsage updatePartUsage(
            Long id,
            PartUsageRequest request) {

        if (request.getQuantity() == null ||
                request.getQuantity() < 1) {

            throw new IllegalArgumentException(
                    "Quantity must be at least 1.");
        }

        // Get authenticated technician
        User currentUser = getCurrentUser();

        // Find existing PartUsage
        PartUsage existing = getPartUsageById(id);

        // Technician must own the existing work order
        WorkOrder existingWorkOrder =
                existing.getWorkOrder();

        if (existingWorkOrder == null) {

            throw new RuntimeException(
                    "Existing part usage has no work order.");
        }

        validateTechnicianWorkOrderAccess(
                existingWorkOrder,
                currentUser
        );

        // Old part and old quantity
        Part oldPart = existing.getPart();

        Integer oldQuantity = existing.getQuantity();

        if (oldQuantity == null) {
            oldQuantity = 0;
        }

        // Find new Part
        Part newPart = partRepository
                .findById(request.getPartId())
                .orElseThrow(() ->
                        new RuntimeException("Part not found"));

        // Find new Work Order
        WorkOrder workOrder = workOrderRepository
                .findById(request.getWorkOrderId())
                .orElseThrow(() ->
                        new RuntimeException("Work order not found"));

        // Technician must be assigned to the new work order too
        validateTechnicianWorkOrderAccess(
                workOrder,
                currentUser
        );

        // =====================================================
        // RESTORE OLD STOCK
        // =====================================================

        Integer oldStock = oldPart.getStockQuantity();

        if (oldStock == null) {
            oldStock = 0;
        }

        oldPart.setStockQuantity(
                oldStock + oldQuantity
        );

        partRepository.save(oldPart);

        // =====================================================
        // CHECK NEW STOCK
        // =====================================================

        Integer newStock = newPart.getStockQuantity();

        if (newStock == null) {
            newStock = 0;
        }

        if (request.getQuantity() > newStock) {

            // Transaction rollback restores old stock
            throw new IllegalArgumentException(
                    "Insufficient stock. Available stock: "
                            + newStock
                            + ", requested quantity: "
                            + request.getQuantity());
        }

        // =====================================================
        // UPDATE NEW PART STOCK
        // =====================================================

        newPart.setStockQuantity(
                newStock - request.getQuantity()
        );

        partRepository.save(newPart);

        // =====================================================
        // UPDATE PART USAGE
        // =====================================================

        existing.setPart(newPart);
        existing.setWorkOrder(workOrder);
        existing.setQuantity(request.getQuantity());

        // Always use actual part unit cost
        BigDecimal unitCost = newPart.getUnitCost();

        if (unitCost == null) {
            unitCost = BigDecimal.ZERO;
        }

        existing.setUnitCost(unitCost);

        // Recalculate total cost
        BigDecimal totalCost = unitCost.multiply(
                BigDecimal.valueOf(request.getQuantity())
        );

        existing.setTotalCost(totalCost);

        return partUsageRepository.save(existing);
    }

    // =========================================================
    // DELETE PART USAGE
    // =========================================================

    @Transactional
    public void deletePartUsage(Long id) {

        // Get authenticated technician
        User currentUser = getCurrentUser();

        // Find existing usage
        PartUsage existing = getPartUsageById(id);

        // Check work order ownership
        WorkOrder workOrder =
                existing.getWorkOrder();

        if (workOrder == null) {

            throw new RuntimeException(
                    "Part usage has no work order.");
        }

        validateTechnicianWorkOrderAccess(
                workOrder,
                currentUser
        );

        Part part = existing.getPart();

        if (part == null) {

            throw new RuntimeException(
                    "Part usage has no associated part.");
        }

        Integer currentStock = part.getStockQuantity();

        if (currentStock == null) {
            currentStock = 0;
        }

        Integer usedQuantity = existing.getQuantity();

        if (usedQuantity == null) {
            usedQuantity = 0;
        }

        // Return used quantity back to stock
        part.setStockQuantity(
                currentStock + usedQuantity
        );

        partRepository.save(part);

        // Delete usage
        partUsageRepository.delete(existing);
    }
}