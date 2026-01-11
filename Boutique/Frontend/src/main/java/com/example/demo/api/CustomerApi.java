package com.example.demo.api;

import com.example.demo.model.CustomerDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.util.List;

public final class CustomerApi {
    private CustomerApi() {}
    
    private static final ObjectMapper M = new ObjectMapper();
    
    public static List<CustomerDto> getAllCustomers() throws Exception {
        HttpResponse<String> res = ApiClient.get("/api/customers");
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), new TypeReference<List<CustomerDto>>() {});
    }
    
    public static CustomerDto getCustomerById(Long id) throws Exception {
        HttpResponse<String> res = ApiClient.get("/api/customers/" + id);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), CustomerDto.class);
    }
    
    public static CustomerDto createCustomer(CustomerDto customer) throws Exception {
        String body = M.writeValueAsString(customer);
        HttpResponse<String> res = ApiClient.post("/api/customers", body);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), CustomerDto.class);
    }
    
    public static CustomerDto updateCustomer(Long id, CustomerDto customer) throws Exception {
        String body = M.writeValueAsString(customer);
        HttpResponse<String> res = ApiClient.put("/api/customers/" + id, body);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        return M.readValue(res.body(), CustomerDto.class);
    }
    
    public static void deleteCustomer(Long id) throws Exception {
        HttpResponse<String> res = ApiClient.delete("/api/customers/" + id);
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
    }
    
    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "<empty body>" : s;
    }
}
