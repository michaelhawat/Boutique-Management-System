package com.csis231.api.service;

import com.csis231.api.dto.OrderDto;
import com.csis231.api.dto.OrderItemDto;
import com.csis231.api.entity.Customer;
import com.csis231.api.entity.Order;
import com.csis231.api.entity.OrderItem;
import com.csis231.api.repository.CustomerRepository;
import com.csis231.api.repository.OrderItemRepository;
import com.csis231.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return convertToDto(order);
    }

    public OrderDto createOrder(OrderDto orderDto) {
        Customer customer = customerRepository.findById(orderDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + orderDto.getCustomerId()));

        Order order = Order.builder()
                .customer(customer)
                .description(orderDto.getDescription())
                .placedAt(LocalDateTime.now())
                .status(Order.OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }

    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (isFinalized(order)) {
            throw new RuntimeException("Cannot update fulfilled or cancelled orders");
        }

        if (orderDto.getStatus() != null) {
            order.setStatus(orderDto.getStatus());
        }

        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be removed");
        }

        orderRepository.delete(order);
    }

    public OrderDto closeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("Cancelled orders cannot be fulfilled");
        }

        if (order.getStatus() == Order.OrderStatus.FULFILLED) {
            throw new RuntimeException("Order already fulfilled");
        }

        List<OrderItem> items = orderItemRepository.findByOrder(order);
        if (items.isEmpty()) {
            throw new RuntimeException("Cannot fulfill order without items");
        }

        order.setStatus(Order.OrderStatus.FULFILLED);
        Order savedOrder = orderRepository.save(order);

        return convertToDto(savedOrder);
    }

    public List<OrderDto> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByPlacedAtBetween(startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private OrderDto convertToDto(Order order) {
        List<OrderItemDto> orderItems = orderItemRepository.findByOrder(order).stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());

        return OrderDto.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomer().getCustomerId())
                .customerName(order.getCustomer().getName())
                .description(order.getDescription())
                .placedAt(order.getPlacedAt())
                .status(order.getStatus())
                .orderItems(orderItems)
                .total(order.getTotal())
                .build();
    }

    private OrderItemDto convertItemToDto(OrderItem item) {
        return OrderItemDto.builder()
                .id(item.getId())
                .orderId(item.getOrder().getOrderId())
                .productId(item.getProduct().getProductId())
                .productName(item.getProductName())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }

    private boolean isFinalized(Order order) {
        return order.getStatus() == Order.OrderStatus.FULFILLED
                || order.getStatus() == Order.OrderStatus.CANCELLED;
    }
}
