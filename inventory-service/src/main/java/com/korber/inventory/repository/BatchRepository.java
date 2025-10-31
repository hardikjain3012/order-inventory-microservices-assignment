package com.korber.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.korber.inventory.entity.Batch;
import com.korber.inventory.entity.Product;

public interface BatchRepository extends JpaRepository<Batch, Long> {
	List<Batch> findByProductOrderByExpiryDateAsc(Product product);
}
