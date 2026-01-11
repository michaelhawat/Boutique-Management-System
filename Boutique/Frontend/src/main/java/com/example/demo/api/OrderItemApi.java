package com.example.demo.api;

import com.example.demo.model.OrderItemDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.util.List;

public final class OrderItemApi {
    private OrderItemApi() {}
    
    private static final ObjectMapper M = new ObjectMapper();
    
    public static List<OrderItemDto> getOrderItems(Long orderId) throws Exception {
        HttpResponse<String> res = ApiClient.get("/api/orders/" + orderId + "/items");
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), new TypeReference<List<OrderItemDto>>() {});
    }
    
    public static OrderItemDto addOrderItem(Long orderId, OrderItemDto orderItem) throws Exception {
        String body = M.writeValueAsString(orderItem);
        HttpResponse<String> res = ApiClient.post("/api/orders/" + orderId + "/items", body);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), OrderItemDto.class);
    }
    
    public static OrderItemDto updateOrderItem(Long orderId, Long itemId, OrderItemDto orderItem) throws Exception {
        String body = M.writeValueAsString(orderItem);
        HttpResponse<String> res = ApiClient.put("/api/orders/" + orderId + "/items/" + itemId, body);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), OrderItemDto.class);
    }
    
    public static void removeOrderItem(Long orderId, Long itemId) throws Exception {
        HttpResponse<String> res = ApiClient.delete("/api/orders/" + orderId + "/items/" + itemId);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
    }
    
    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "<empty body>" : s;
    }
}
