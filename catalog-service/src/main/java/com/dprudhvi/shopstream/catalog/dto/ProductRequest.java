package com.dprudhvi.shopstream.catalog.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequest(@NotBlank @Size(max = 255) String name, String description, @NotNull @Positive BigDecimal price) {
        
}
    

