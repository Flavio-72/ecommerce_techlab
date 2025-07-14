package com.techlab.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techlab.ecommerce.model.entity.AppUser;
import com.techlab.ecommerce.model.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUser(AppUser user);

}