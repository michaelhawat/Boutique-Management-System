package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class OrderItemDto {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("orderId")
    private Long orderId;
    
    @JsonProperty("productId")
    private Long productId;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("unitPrice")
    private BigDecimal unitPrice;
    
    @JsonProperty("quantity")
    private Integer quantity;
    
    @JsonProperty("subtotal")
    private BigDecimal subtotal;
    
    public OrderItemDto() {}
    
    public OrderItemDto(Long id,
                        Long orderId,
                        Long productId,
                        String productName,
                        BigDecimal unitPrice,
                        Integer quantity,
                        BigDecimal subtotal) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

}
