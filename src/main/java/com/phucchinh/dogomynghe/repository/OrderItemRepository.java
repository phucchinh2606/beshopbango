// OrderItemRepository.java
package com.phucchinh.dogomynghe.repository;
import com.phucchinh.dogomynghe.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}