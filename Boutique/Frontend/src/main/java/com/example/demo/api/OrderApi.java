package com.example.demo.api;

import com.example.demo.model.OrderDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public final class OrderApi {
    private OrderApi() {}

    private static final ObjectMapper M = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    
    public static List<OrderDto> getAllOrders() throws Exception {
        HttpResponse<String> res = ApiClient.get("/api/orders");
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), new TypeReference<List<OrderDto>>() {});
    }
    
    public static OrderDto getOrderById(Long id) throws Exception {
        HttpResponse<String> res = ApiClient.get("/api/orders/" + id);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), OrderDto.class);
    }
    
    public static List<OrderDto> getOrdersByStatus(String status) throws Exception {
        HttpResponse<String> res = ApiClient.get("/api/orders/status/" + status);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), new TypeReference<List<OrderDto>>() {});
    }
    
    public static OrderDto createOrder(OrderDto order) throws Exception {
        String body = M.writeValueAsString(order);
        HttpResponse<String> res = ApiClient.post("/api/orders", body);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), OrderDto.class);
    }
    
    public static OrderDto updateOrder(Long id, OrderDto order) throws Exception {
        String body = M.writeValueAsString(order);
        HttpResponse<String> res = ApiClient.put("/api/orders/" + id, body);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), OrderDto.class);
    }
    
    public static OrderDto closeOrder(Long id) throws Exception {
        HttpResponse<String> res = ApiClient.post("/api/orders/" + id + "/close", "");
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), OrderDto.class);
    }
    
    public static void deleteOrder(Long id) throws Exception {
        HttpResponse<String> res = ApiClient.delete("/api/orders/" + id);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
    }

    public static List<OrderDto> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        String start = java.net.URLEncoder.encode(startDate.toString(), "UTF-8");
        String end = java.net.URLEncoder.encode(endDate.toString(), "UTF-8");
        HttpResponse<String> res = ApiClient.get("/api/orders/date-range?startDate=" + start + "&endDate=" + end);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), new TypeReference<List<OrderDto>>() {});
    }
    
    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "<empty body>" : s;
    }
}
