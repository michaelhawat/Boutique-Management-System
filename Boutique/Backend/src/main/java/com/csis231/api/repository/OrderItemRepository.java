package com.csis231.api.repository;

import com.csis231.api.entity.Order;
import com.csis231.api.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    void deleteByOrder(Order order);
}
