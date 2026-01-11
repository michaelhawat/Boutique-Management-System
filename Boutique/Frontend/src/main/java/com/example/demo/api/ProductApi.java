package com.example.demo.api;

import com.example.demo.model.ProductDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.util.List;

public final class ProductApi {
    private ProductApi() {}
    
    private static final ObjectMapper M = new ObjectMapper();
    
    public static List<ProductDto> getAllProducts() throws Exception {
        HttpResponse<String> res = ApiClient.get("/api/products");
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), new TypeReference<List<ProductDto>>() {});
    }
    
    public static ProductDto getProductById(Long id) throws Exception {
        HttpResponse<String> res = ApiClient.get("/api/products/" + id);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), ProductDto.class);
    }
    
    public static List<ProductDto> getProductsByCategory(String category) throws Exception {
        HttpResponse<String> res = ApiClient.get("/api/products/category/" + category);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), new TypeReference<List<ProductDto>>() {});
    }
    
    public static List<ProductDto> getAvailableProducts() throws Exception {
        HttpResponse<String> res = ApiClient.get("/api/products/available");
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), new TypeReference<List<ProductDto>>() {});
    }
    
    public static ProductDto createProduct(ProductDto product) throws Exception {
        String body = M.writeValueAsString(product);
        HttpResponse<String> res = ApiClient.post("/api/products", body);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), ProductDto.class);
    }
    
    public static ProductDto updateProduct(Long id, ProductDto product) throws Exception {
        String body = M.writeValueAsString(product);
        HttpResponse<String> res = ApiClient.put("/api/products/" + id, body);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), ProductDto.class);
    }
    
    public static void deleteProduct(Long id) throws Exception {
        HttpResponse<String> res = ApiClient.delete("/api/products/" + id);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
    }
    
    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "<empty body>" : s;
    }
}
