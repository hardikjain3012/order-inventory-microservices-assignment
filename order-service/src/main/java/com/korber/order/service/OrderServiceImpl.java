package com.korber.order.service;

import com.korber.order.dto.OrderItemRequest;
import com.korber.order.dto.OrderRequest;
import com.korber.order.dto.OrderResponse;
import com.korber.order.entity.Order;
import com.korber.order.entity.OrderItem;
import com.korber.order.exceptions.OrderProcessingException;
import com.korber.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        try {
            // Update inventory first
            for (OrderItemRequest item : request.getItems()) {
                inventoryService.decrementStock(item.getProductId(), item.getQuantity());
            }

            Order order = new Order();
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PLACED");
                    order.setCustomerName(request.getCustomerName());

            order.setItems(request.getItems().stream()
                    .map(i -> OrderItem.builder()
                            .productId(i.getProductId())
                            .quantity(i.getQuantity())
                            .order(order)
                            .build())
                    .collect(Collectors.toList()));

            orderRepository.save(order);

            return OrderResponse.builder()
                    .orderId(order.getId())
                    .orderDate(order.getOrderDate())
                    .status(order.getStatus())
                    .customerName(order.getCustomerName())
                    .items(request.getItems())
                    .build();

        } catch (Exception e) {
            throw new OrderProcessingException("Failed to place order: " + e.getMessage());
        }
    }
}
