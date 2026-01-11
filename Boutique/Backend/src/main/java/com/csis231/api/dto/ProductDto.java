package com.csis231.api.dto;

import com.csis231.api.entity.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long productId;
    
    @NotBlank
    private String name;
    
    @NotNull
    private ProductCategory category;
    
    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQty;
}
