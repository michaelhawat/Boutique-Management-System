package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerDto {
    @JsonProperty("customerId")
    private Long customerId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("contact")
    private String contact;
    
    public CustomerDto() {}
    
    public CustomerDto(Long customerId, String name, String contact) {
        this.customerId = customerId;
        this.name = name;
        this.contact = contact;
    }
    
    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
