package com.csis231.api.service;

import com.csis231.api.dto.OrderItemDto;
import com.csis231.api.entity.*;
import com.csis231.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderItemService {
    
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    
    public List<OrderItemDto> getOrderItems(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        return orderItemRepository.findByOrder(order).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public OrderItemDto addOrderItem(Long orderId, OrderItemDto orderItemDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // Check if order is pending
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Items can only be added to pending orders");
        }
        
        Product product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + orderItemDto.getProductId()));
        
        // Check stock availability
        if (product.getStockQty() < orderItemDto.getQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        
        // Capture unit price at the moment of sale
        BigDecimal unitPrice = product.getPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(orderItemDto.getQuantity()));
        
        // Create order item
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .productName(product.getName())
                .unitPrice(unitPrice)
                .quantity(orderItemDto.getQuantity())
                .subtotal(subtotal)
                .build();
        
        OrderItem savedItem = orderItemRepository.save(orderItem);
        
        // Update stock
        product.setStockQty(product.getStockQty() - orderItemDto.getQuantity());
        productRepository.save(product);
        
        return convertToDto(savedItem);
    }
    
    public OrderItemDto updateOrderItem(Long orderId, Long itemId, OrderItemDto orderItemDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // Check if order is pending
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Items can only be updated on pending orders");
        }
        
        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + itemId));
        
        // Verify item belongs to order
        if (!orderItem.getOrder().getOrderId().equals(orderId)) {
            throw new RuntimeException("Order item does not belong to this order");
        }
        
        Product product = orderItem.getProduct();
        int oldQuantity = orderItem.getQuantity();
        int newQuantity = orderItemDto.getQuantity();
        
        // Check stock availability for quantity change
        if (newQuantity > oldQuantity) {
            int additionalQuantity = newQuantity - oldQuantity;
            if (product.getStockQty() < additionalQuantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }
        
        // Update order item
        orderItem.setQuantity(newQuantity);
        orderItem.setSubtotal(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
        
        OrderItem savedItem = orderItemRepository.save(orderItem);
        
        // Update stock
        int stockChange = newQuantity - oldQuantity;
        product.setStockQty(product.getStockQty() - stockChange);
        productRepository.save(product);
        
        return convertToDto(savedItem);
    }
    
    public void removeOrderItem(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // Check if order is pending
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Items can only be removed from pending orders");
        }
        
        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + itemId));
        
        // Verify item belongs to order
        if (!orderItem.getOrder().getOrderId().equals(orderId)) {
            throw new RuntimeException("Order item does not belong to this order");
        }
        
        // Restore stock
        Product product = orderItem.getProduct();
        product.setStockQty(product.getStockQty() + orderItem.getQuantity());
        productRepository.save(product);
        
        // Remove order item
        orderItemRepository.delete(orderItem);
    }
    
    private OrderItemDto convertToDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getOrderId())
                .productId(orderItem.getProduct().getProductId())
                .productName(orderItem.getProductName())
                .unitPrice(orderItem.getUnitPrice())
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getSubtotal())
                .build();
    }
}
