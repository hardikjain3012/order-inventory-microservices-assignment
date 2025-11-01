package com.korber.order.service;

import com.korber.order.dto.OrderRequest;
import com.korber.order.dto.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request);
}
