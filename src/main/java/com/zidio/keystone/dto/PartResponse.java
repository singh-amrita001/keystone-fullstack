package com.zidio.keystone.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PartResponse {

    private Long id;
    private String name;
    private String partNumber;
    private String description;
    private Integer stockQuantity;
    private BigDecimal unitCost;
    private Boolean active;
    private LocalDateTime createdAt;

    public PartResponse() {
    }

    public PartResponse(
            Long id,
            String name,
            String partNumber,
            String description,
            Integer stockQuantity,
            BigDecimal unitCost,
            Boolean active,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.partNumber = partNumber;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.unitCost = unitCost;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
