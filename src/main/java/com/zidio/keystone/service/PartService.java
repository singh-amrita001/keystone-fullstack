package com.zidio.keystone.service;

import com.zidio.keystone.domain.Part;
import com.zidio.keystone.repository.PartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Part createPart(Part part) {

        if (part.getPartNumber() != null
                && partRepository.existsByPartNumber(part.getPartNumber())) {

            throw new RuntimeException("Part number already exists");
        }

        return partRepository.save(part);
    }

    public Part getPartById(Long id) {

        return partRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Part not found"));
    }

    // Get Parts - Pagination + Search
    public Page<Part> getAllParts(
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
                    );
        }

        return partRepository.findAll(pageable);
    }

    public Part updatePart(Long id, Part updatedPart) {

        Part existingPart = getPartById(id);

        if (updatedPart.getPartNumber() != null
                && !updatedPart.getPartNumber()
                        .equals(existingPart.getPartNumber())
                && partRepository.existsByPartNumber(
                        updatedPart.getPartNumber())) {

            throw new RuntimeException("Part number already exists");
        }

        existingPart.setName(updatedPart.getName());
        existingPart.setPartNumber(updatedPart.getPartNumber());
        existingPart.setDescription(updatedPart.getDescription());
        existingPart.setStockQuantity(updatedPart.getStockQuantity());
        existingPart.setUnitCost(updatedPart.getUnitCost());
        existingPart.setActive(updatedPart.getActive());

        return partRepository.save(existingPart);
    }

    public void deletePart(Long id) {

        Part existingPart = getPartById(id);

        partRepository.delete(existingPart);
    }
}