package com.csis231.api.repository;

import com.csis231.api.entity.Product;
import com.csis231.api.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByStockQtyGreaterThan(Integer stockQty);
}
