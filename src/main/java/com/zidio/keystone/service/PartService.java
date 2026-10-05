package com.zidio.keystone.service;

import com.zidio.keystone.domain.Part;
import com.zidio.keystone.dto.PartResponse;
import com.zidio.keystone.repository.PartRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    // =====================================================
    // CREATE PART
    // =====================================================

    public PartResponse createPart(Part part) {

        if (part.getPartNumber() != null
                && partRepository.existsByPartNumber(
                        part.getPartNumber().trim())) {

            throw new RuntimeException(
                    "Part number already exists"
            );
        }

        part.setName(part.getName().trim());

        if (part.getPartNumber() != null) {
            part.setPartNumber(
                    part.getPartNumber().trim()
            );
        }

        Part savedPart = partRepository.save(part);

        return convertToResponse(savedPart);
    }

    // =====================================================
    // GET PART BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public PartResponse getPartById(Long id) {

        Part part = partRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Part not found")
                );

        return convertToResponse(part);
    }

    // =====================================================
    // GET PARTS - PAGINATION + SEARCH
    // =====================================================

    @Transactional(readOnly = true)
    public Page<PartResponse> getAllParts(
            int page,
            int size,
            String search) {

        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.trim().isEmpty()) {

            return partRepository
                    .findByNameContainingIgnoreCaseOrPartNumberContainingIgnoreCase(
                            search.trim(),
                            search.trim(),
                            pageable
                    )
                    .map(this::convertToResponse);
        }

        return partRepository
                .findAll(pageable)
                .map(this::convertToResponse);
    }

    // =====================================================
    // UPDATE PART
    // =====================================================

    public PartResponse updatePart(
            Long id,
            Part updatedPart) {

        Part existingPart = partRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Part not found")
                );

        String partNumber = updatedPart.getPartNumber();

        if (partNumber != null) {
            partNumber = partNumber.trim();

            if (!partNumber.equals(existingPart.getPartNumber())
                    && partRepository.existsByPartNumber(partNumber)) {

                throw new RuntimeException(
                        "Part number already exists"
                );
            }
        }

        existingPart.setName(
                updatedPart.getName().trim()
        );

        existingPart.setPartNumber(partNumber);

        existingPart.setDescription(
                updatedPart.getDescription()
        );

        existingPart.setStockQuantity(
                updatedPart.getStockQuantity()
        );

        existingPart.setUnitCost(
                updatedPart.getUnitCost()
        );

        existingPart.setActive(
                updatedPart.getActive()
        );

        Part savedPart = partRepository.save(existingPart);

        return convertToResponse(savedPart);
    }

    // =====================================================
    // DELETE PART
    // =====================================================

    public void deletePart(Long id) {

        Part existingPart = partRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Part not found")
                );

        partRepository.delete(existingPart);
    }

    // =====================================================
    // CONVERT ENTITY TO RESPONSE DTO
    // =====================================================

    private PartResponse convertToResponse(Part part) {

        return new PartResponse(
                part.getId(),
                part.getName(),
                part.getPartNumber(),
                part.getDescription(),
                part.getStockQuantity(),
                part.getUnitCost(),
                part.getActive(),
                part.getCreatedAt()
        );
    }
}