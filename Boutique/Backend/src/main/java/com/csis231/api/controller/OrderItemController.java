package com.csis231.api.controller;

import com.csis231.api.dto.OrderItemDto;
import com.csis231.api.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
@Slf4j
public class OrderItemController {
    
    private final OrderItemService orderItemService;
    
    @GetMapping
    public ResponseEntity<List<OrderItemDto>> getOrderItems(@PathVariable Long orderId) {
        try {
            List<OrderItemDto> items = orderItemService.getOrderItems(orderId);
            return ResponseEntity.ok(items);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(List.of());
        } catch (Exception e) {
            log.error("Error fetching order items for order: " + orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> addOrderItem(@PathVariable Long orderId, @Valid @RequestBody OrderItemDto orderItemDto) {
        try {
            OrderItemDto createdItem = orderItemService.addOrderItem(orderId, orderItemDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage(), "code", "ORDER_ITEM_ADD_FAILED"));
        } catch (Exception e) {
            log.error("Error adding order item to order: " + orderId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid order item data", "code", "INVALID_DATA"));
        }
    }
    
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateOrderItem(@PathVariable Long orderId, @PathVariable Long itemId, 
                                          @Valid @RequestBody OrderItemDto orderItemDto) {
        try {
            OrderItemDto updatedItem = orderItemService.updateOrderItem(orderId, itemId, orderItemDto);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage(), "code", "ORDER_ITEM_UPDATE_FAILED"));
        } catch (Exception e) {
            log.error("Error updating order item: " + itemId + " for order: " + orderId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid order item data", "code", "INVALID_DATA"));
        }
    }
    
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        try {
            orderItemService.removeOrderItem(orderId, itemId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage(), "code", "ORDER_ITEM_REMOVE_FAILED"));
        } catch (Exception e) {
            log.error("Error removing order item: " + itemId + " from order: " + orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error", "code", "INTERNAL_ERROR"));
        }
    }
}
