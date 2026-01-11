package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDto {
    @JsonProperty("userId")
    private Long userId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("contact")
    private String contact;
    
    public UserDto() {}
    
    public UserDto(Long userId, String name, String role, String contact) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.contact = contact;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
