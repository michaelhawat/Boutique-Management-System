package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("customerId")
    private Long customerId;

    @JsonProperty("customerName")
    private String customerName;


    @JsonProperty("description")
    private String description;

    @JsonProperty("placedAt")
    private LocalDateTime placedAt;

    @JsonProperty("status")
    private String status;

    @JsonProperty("orderItems")
    private List<OrderItemDto> orderItems;

    @JsonProperty("total")
    private BigDecimal total;

    public OrderDto() {}

    public OrderDto(Long orderId,
                    Long customerId,
                    String customerName,
                    String description,
                    LocalDateTime placedAt,
                    String status,
                    List<OrderItemDto> orderItems,
                    BigDecimal total) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.description = description;
        this.placedAt = placedAt;
        this.status = status;
        this.orderItems = orderItems;
        this.total = total;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getPlacedAt() { return placedAt; }
    public void setPlacedAt(LocalDateTime placedAt) { this.placedAt = placedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<OrderItemDto> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDto> orderItems) { this.orderItems = orderItems; }

    public BigDecimal getTotal() {
        if (total != null) {
            return total;
        }
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderItems.stream()
                .map(OrderItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
