package com.korber.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}
