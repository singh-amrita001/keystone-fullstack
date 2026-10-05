package com.zidio.keystone.controller;

import com.zidio.keystone.domain.Customer;
import com.zidio.keystone.service.CustomerService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // =========================
    // CREATE CUSTOMER
    // ADMIN / DISPATCHER ONLY
    // =========================
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Customer> createCustomer(
            @RequestBody Customer customer) {

        Customer createdCustomer =
                customerService.createCustomer(customer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCustomer);
    }

    // =========================
    // GET CUSTOMERS
    //
    // Example:
    // GET /api/customers?page=0&size=10
    //
    // Search:
    // GET /api/customers?search=Amrita&page=0&size=10
    // =========================
    @GetMapping
    public ResponseEntity<Page<Customer>> getCustomers(
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
                customerService.getCustomers(
                        search,
                        pageable,
                        authentication.getName()
                )
        );
    }

    // =========================
    // GET CUSTOMER BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                customerService.getCustomerById(
                        id,
                        authentication.getName()
                )
        );
    }

    // =========================
    // UPDATE CUSTOMER
    // ADMIN / DISPATCHER ONLY
    // =========================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @RequestBody Customer customer) {

        return ResponseEntity.ok(
                customerService.updateCustomer(
                        id,
                        customer
                )
        );
    }

    // =========================
    // DELETE CUSTOMER
    // ADMIN / DISPATCHER ONLY
    // =========================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<?> deleteCustomer(
            @PathVariable Long id) {

        try {

            customerService.deleteCustomer(id);

            return ResponseEntity.noContent().build();

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }
}

