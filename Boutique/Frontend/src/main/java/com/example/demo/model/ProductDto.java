package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class ProductDto {
    @JsonProperty("productId")
    private Long productId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("category")
    private ProductCategory category;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("stockQty")
    private Integer stockQty;
    
    public ProductDto() {}
    
    public ProductDto(Long productId, String name, ProductCategory category, BigDecimal price, Integer stockQty) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQty = stockQty;
    }
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQty() { return stockQty; }
    public void setStockQty(Integer stockQty) { this.stockQty = stockQty; }
}
