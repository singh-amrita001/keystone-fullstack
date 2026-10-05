package com.zidio.keystone.controller;

import com.zidio.keystone.domain.Part;
import com.zidio.keystone.dto.PartResponse;
import com.zidio.keystone.service.PartService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    // =====================================================
    // CREATE PART
    // ADMIN / DISPATCHER ONLY
    // =====================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<PartResponse> createPart(
            @Valid @RequestBody Part part) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                partService.createPart(part)
        );
    }

    // =====================================================
    // GET PART BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> getPartById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                partService.getPartById(id)
        );
    }

    // =====================================================
    // GET PARTS - PAGINATION + SEARCH
    // =====================================================

    @GetMapping
    public ResponseEntity<Page<PartResponse>> getAllParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(
                partService.getAllParts(
                        page,
                        size,
                        search
                )
        );
    }

    // =====================================================
    // UPDATE PART
    // ADMIN / DISPATCHER ONLY
    // =====================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<PartResponse> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody Part part) {

        return ResponseEntity.ok(
                partService.updatePart(id, part)
        );
    }

    // =====================================================
    // DELETE PART
    // ADMIN / DISPATCHER ONLY
    // =====================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Void> deletePart(
            @PathVariable Long id) {

        partService.deletePart(id);

        return ResponseEntity.noContent().build();
    }
}