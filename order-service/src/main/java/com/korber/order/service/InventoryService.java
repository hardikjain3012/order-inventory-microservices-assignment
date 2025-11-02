package com.korber.order.service;

public interface InventoryService {
    void decrementStock(Long productId, Integer quantity);
}
