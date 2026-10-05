package com.zidio.keystone.controller;

import com.zidio.keystone.domain.Part;
import com.zidio.keystone.service.PartService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    // Create Part
    @PostMapping
    public ResponseEntity<Part> createPart(
            @Valid @RequestBody Part part) {

        return ResponseEntity.ok(
                partService.createPart(part)
        );
    }

    // Get Part by ID
    @GetMapping("/{id}")
    public ResponseEntity<Part> getPartById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                partService.getPartById(id)
        );
    }

    // Get Parts - Pagination + Search
    @GetMapping
    public ResponseEntity<Page<Part>> getAllParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(
                partService.getAllParts(page, size, search)
        );
    }

    // Update Part
    @PutMapping("/{id}")
    public ResponseEntity<Part> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody Part part) {

        return ResponseEntity.ok(
                partService.updatePart(id, part)
        );
    }

    // Delete Part
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePart(
            @PathVariable Long id) {

        partService.deletePart(id);

        return ResponseEntity.ok(
                "Part deleted successfully"
        );
    }
}