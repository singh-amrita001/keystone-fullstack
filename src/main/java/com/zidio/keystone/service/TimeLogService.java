package com.zidio.keystone.service;

import com.zidio.keystone.domain.TimeLog;
import com.zidio.keystone.domain.WorkOrder;
import com.zidio.keystone.dto.TimeLogRequest;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.TimeLogRepository;
import com.zidio.keystone.repository.UserRepository;
import com.zidio.keystone.repository.WorkOrderRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TimeLogService {

    private final TimeLogRepository timeLogRepository;
    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;

    public TimeLogService(
            TimeLogRepository timeLogRepository,
            WorkOrderRepository workOrderRepository,
            UserRepository userRepository) {

        this.timeLogRepository = timeLogRepository;
        this.workOrderRepository = workOrderRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // CREATE TIME LOG
    // =====================================================

    @Transactional
    public TimeLog createTimeLog(TimeLogRequest request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User technician = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"));

        String role = technician.getRole();

        if (role == null ||
                !role.equalsIgnoreCase("TECHNICIAN")) {

            throw new AccessDeniedException(
                    "Only technicians can create time logs.");
        }

        WorkOrder workOrder =
                workOrderRepository.findById(
                        request.getWorkOrderId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));

        // Technician can log time only for their assigned work order
        if (workOrder.getTechnician() == null ||
                !workOrder.getTechnician()
                        .getId()
                        .equals(technician.getId())) {

            throw new AccessDeniedException(
                    "You can only log time for work orders assigned to you.");
        }

        TimeLog timeLog = new TimeLog();

        timeLog.setWorkOrder(workOrder);
        timeLog.setTechnician(technician);
        timeLog.setMinutes(request.getMinutes());
        timeLog.setNote(request.getNote());

        return timeLogRepository.save(timeLog);
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public TimeLog getTimeLogById(Long id) {

        return timeLogRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Time log not found"));
    }

    // =====================================================
    // GET ALL TIME LOGS - PAGINATED
    // =====================================================

    @Transactional(readOnly = true)
    public Page<TimeLog> getAllTimeLogs(
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return timeLogRepository.findAll(pageable);
    }

    // =====================================================
    // GET BY WORK ORDER
    // =====================================================

    @Transactional(readOnly = true)
    public List<TimeLog> getByWorkOrderId(
            Long workOrderId) {

        return timeLogRepository
                .findByWorkOrderId(workOrderId);
    }

    // =====================================================
    // GET BY TECHNICIAN
    // =====================================================

    @Transactional(readOnly = true)
    public List<TimeLog> getByTechnicianId(
            Long technicianId) {

        return timeLogRepository
                .findByTechnicianId(technicianId);
    }

    // =====================================================
    // UPDATE TIME LOG
    // =====================================================

    @Transactional
    public TimeLog updateTimeLog(
            Long id,
            TimeLogRequest request) {

        TimeLog existing =
                getTimeLogById(id);

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User currentUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Authenticated user not found"));

        String role = currentUser.getRole();

        // Only the technician who created the log can update it
        if (role == null ||
                !role.equalsIgnoreCase("TECHNICIAN") ||
                existing.getTechnician() == null ||
                !existing.getTechnician()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You can only update your own time logs.");
        }

        WorkOrder workOrder =
                workOrderRepository.findById(
                        request.getWorkOrderId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));

        // Technician must still be assigned to the work order
        if (workOrder.getTechnician() == null ||
                !workOrder.getTechnician()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You can only log time for work orders assigned to you.");
        }

        existing.setWorkOrder(workOrder);
        existing.setMinutes(request.getMinutes());
        existing.setNote(request.getNote());

        return timeLogRepository.save(existing);
    }

    // =====================================================
    // DELETE TIME LOG
    // =====================================================

    @Transactional
    public void deleteTimeLog(Long id) {

        TimeLog existing =
                getTimeLogById(id);

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User currentUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Authenticated user not found"));

        String role = currentUser.getRole();

        // Only the technician who created the log can delete it
        if (role == null ||
                !role.equalsIgnoreCase("TECHNICIAN") ||
                existing.getTechnician() == null ||
                !existing.getTechnician()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You can only delete your own time logs.");
        }

        timeLogRepository.delete(existing);
    }

    // =====================================================
    // TOTAL MINUTES FOR A WORK ORDER
    // =====================================================

    @Transactional(readOnly = true)
    public Integer getTotalMinutesByWorkOrderId(
            Long workOrderId) {

        List<TimeLog> logs =
                timeLogRepository
                        .findByWorkOrderId(workOrderId);

        return logs.stream()
                .map(TimeLog::getMinutes)
                .filter(minutes -> minutes != null)
                .mapToInt(Integer::intValue)
                .sum();
    }
}

