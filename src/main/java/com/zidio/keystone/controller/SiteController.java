package com.zidio.keystone.controller;

import com.zidio.keystone.dto.SiteRequest;
import com.zidio.keystone.dto.SiteResponse;
import com.zidio.keystone.service.SiteService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sites")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    // =====================================================
    // CREATE SITE
    // ADMIN / DISPATCHER ONLY
    // =====================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<SiteResponse> createSite(
            @Valid @RequestBody SiteRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                siteService.createSite(request)
        );
    }

    // =====================================================
    // GET SITE BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<SiteResponse> getSiteById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                siteService.getSiteById(
                        id,
                        authentication.getName()
                )
        );
    }

    // =====================================================
    // GET ALL SITES - PAGINATED
    // =====================================================

    @GetMapping
    public ResponseEntity<Page<SiteResponse>> getSites(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        return ResponseEntity.ok(
                siteService.getSites(
                        search,
                        pageable,
                        authentication.getName()
                )
        );
    }

    // =====================================================
    // GET SITES BY CUSTOMER
    // =====================================================

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<SiteResponse>> getSitesByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        return ResponseEntity.ok(
                siteService.getSitesByCustomerId(
                        customerId,
                        pageable,
                        authentication.getName()
                )
        );
    }

    // =====================================================
    // UPDATE SITE
    // ADMIN / DISPATCHER ONLY
    // =====================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<SiteResponse> updateSite(
            @PathVariable Long id,
            @Valid @RequestBody SiteRequest request) {

        return ResponseEntity.ok(
                siteService.updateSite(id, request)
        );
    }

    // =====================================================
    // DELETE SITE
    // ADMIN / DISPATCHER ONLY
    // =====================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Void> deleteSite(
            @PathVariable Long id) {

        siteService.deleteSite(id);

        return ResponseEntity.noContent().build();
    }
}