package com.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cart.entity.OrderData;

public interface OrderRepository extends JpaRepository<OrderData, Long> {

	List<OrderData> findByUserId(String userId);
}
