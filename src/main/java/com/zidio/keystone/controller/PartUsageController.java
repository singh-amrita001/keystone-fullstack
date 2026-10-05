package com.zidio.keystone.controller;

import com.zidio.keystone.domain.PartUsage;
import com.zidio.keystone.dto.PartUsageRequest;
import com.zidio.keystone.service.PartUsageService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/part-usages")
public class PartUsageController {

    private final PartUsageService partUsageService;

    public PartUsageController(PartUsageService partUsageService) {
        this.partUsageService = partUsageService;
    }

    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    public ResponseEntity<PartUsage> createPartUsage(
            @Valid @RequestBody PartUsageRequest request) {

        return ResponseEntity.ok(
                partUsageService.createPartUsage(request)
        );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<PartUsage> getPartUsageById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                partUsageService.getPartUsageById(id)
        );
    }

    // =========================================================
    // GET ALL - PAGINATION
    // =========================================================

    @GetMapping
    public ResponseEntity<Page<PartUsage>> getAllPartUsages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                partUsageService.getAllPartUsages(page, size)
        );
    }

    // =========================================================
    // GET BY WORK ORDER
    // =========================================================

    @GetMapping("/work-order/{workOrderId}")
    public ResponseEntity<List<PartUsage>> getByWorkOrderId(
            @PathVariable Long workOrderId) {

        return ResponseEntity.ok(
                partUsageService.getByWorkOrderId(workOrderId)
        );
    }

    // =========================================================
    // GET BY PART
    // =========================================================

    @GetMapping("/part/{partId}")
    public ResponseEntity<List<PartUsage>> getByPartId(
            @PathVariable Long partId) {

        return ResponseEntity.ok(
                partUsageService.getByPartId(partId)
        );
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<PartUsage> updatePartUsage(
            @PathVariable Long id,
            @Valid @RequestBody PartUsageRequest request) {

        return ResponseEntity.ok(
                partUsageService.updatePartUsage(id, request)
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePartUsage(
            @PathVariable Long id) {

        partUsageService.deletePartUsage(id);

        return ResponseEntity.ok(
                "Part usage deleted successfully"
        );
    }
}