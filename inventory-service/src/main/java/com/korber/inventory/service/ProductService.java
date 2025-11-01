package com.korber.inventory.service;

import java.util.List;

import com.korber.inventory.entity.Product;

public interface ProductService {
    Product create(Product product);
    Product update(Long id, Product product);
    void delete(Long id);
    Product getById(Long id);
    List<Product> getAll();
}

