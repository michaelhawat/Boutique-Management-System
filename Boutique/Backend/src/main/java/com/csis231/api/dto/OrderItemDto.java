package com.csis231.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Long id;
    
    @NotNull
    private Long orderId;
    
    @NotNull
    private Long productId;

    private String productName;

    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    @NotNull
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Subtotal must be greater than 0")
    private BigDecimal subtotal;
}
