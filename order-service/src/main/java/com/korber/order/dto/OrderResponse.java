package com.korber.order.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private String status;
    private LocalDateTime orderDate;
    private String customerName;
    private List<OrderItemRequest> items;
}
