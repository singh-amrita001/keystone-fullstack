package com.zidio.keystone.controller;

import com.zidio.keystone.domain.TimeLog;
import com.zidio.keystone.dto.TimeLogRequest;
import com.zidio.keystone.service.TimeLogService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-logs")
public class TimeLogController {

    private final TimeLogService timeLogService;

    public TimeLogController(TimeLogService timeLogService) {
        this.timeLogService = timeLogService;
    }

    // =====================================================
    // CREATE TIME LOG
    // =====================================================

    @PostMapping
    public ResponseEntity<TimeLog> createTimeLog(
            @Valid @RequestBody TimeLogRequest request) {

        return ResponseEntity.ok(
                timeLogService.createTimeLog(request)
        );
    }

    // =====================================================
    // GET TIME LOG BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<TimeLog> getTimeLogById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                timeLogService.getTimeLogById(id)
        );
    }

    // =====================================================
    // GET ALL TIME LOGS - PAGINATED
    // =====================================================

    @GetMapping
    public ResponseEntity<Page<TimeLog>> getAllTimeLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                timeLogService.getAllTimeLogs(page, size)
        );
    }

    // =====================================================
    // GET TIME LOGS BY WORK ORDER
    // =====================================================

    @GetMapping("/work-order/{workOrderId}")
    public ResponseEntity<List<TimeLog>> getByWorkOrderId(
            @PathVariable Long workOrderId) {

        return ResponseEntity.ok(
                timeLogService.getByWorkOrderId(workOrderId)
        );
    }

    // =====================================================
    // GET TIME LOGS BY TECHNICIAN
    // =====================================================

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<TimeLog>> getByTechnicianId(
            @PathVariable Long technicianId) {

        return ResponseEntity.ok(
                timeLogService.getByTechnicianId(technicianId)
        );
    }

    // =====================================================
    // GET TOTAL MINUTES FOR WORK ORDER
    // =====================================================

    @GetMapping("/work-order/{workOrderId}/total-minutes")
    public ResponseEntity<Integer> getTotalMinutesByWorkOrderId(
            @PathVariable Long workOrderId) {

        return ResponseEntity.ok(
                timeLogService.getTotalMinutesByWorkOrderId(
                        workOrderId
                )
        );
    }

    // =====================================================
    // UPDATE TIME LOG
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<TimeLog> updateTimeLog(
            @PathVariable Long id,
            @Valid @RequestBody TimeLogRequest request) {

        return ResponseEntity.ok(
                timeLogService.updateTimeLog(id, request)
        );
    }

    // =====================================================
    // DELETE TIME LOG
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTimeLog(
            @PathVariable Long id) {

        timeLogService.deleteTimeLog(id);

        return ResponseEntity.ok(
                "Time log deleted successfully"
        );
    }
}
