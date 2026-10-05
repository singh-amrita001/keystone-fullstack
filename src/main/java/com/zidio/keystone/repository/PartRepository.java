package com.zidio.keystone.repository;

import com.zidio.keystone.domain.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {

    Optional<Part> findByPartNumber(String partNumber);

    boolean existsByPartNumber(String partNumber);

    Page<Part> findByNameContainingIgnoreCaseOrPartNumberContainingIgnoreCase(
            String name,
            String partNumber,
            Pageable pageable
    );
}