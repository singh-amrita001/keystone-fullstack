package com.zidio.keystone.repository;

import com.zidio.keystone.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

