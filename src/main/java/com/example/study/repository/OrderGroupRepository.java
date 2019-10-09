package com.example.study.repository;

import com.example.study.model.entity.OrderGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long> {
}
