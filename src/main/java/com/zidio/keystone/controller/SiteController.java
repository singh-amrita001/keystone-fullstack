package com.zidio.keystone.controller;

import com.zidio.keystone.domain.Site;
import com.zidio.keystone.dto.SiteRequest;
import com.zidio.keystone.service.SiteService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    public ResponseEntity<Site> createSite(
            @Valid @RequestBody SiteRequest request) {

        return ResponseEntity.ok(
                siteService.createSite(request)
        );
    }

    // =====================================================
    // GET SITE BY ID
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<Site> getSiteById(
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
    // GET ALL SITES
    //
    // Example:
    // /api/sites?page=0&size=10
    //
    // Search:
    // /api/sites?search=Delhi&page=0&size=10
    // =====================================================
    @GetMapping
    public ResponseEntity<Page<Site>> getSites(
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
    public ResponseEntity<Page<Site>> getSitesByCustomerId(
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
    public ResponseEntity<Site> updateSite(
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
    public ResponseEntity<String> deleteSite(
            @PathVariable Long id) {

        siteService.deleteSite(id);

        return ResponseEntity.ok(
                "Site deleted successfully"
        );
    }
}

