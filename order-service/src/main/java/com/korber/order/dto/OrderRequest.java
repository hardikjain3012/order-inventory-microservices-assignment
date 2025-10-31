package com.korber.order.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRequest {
    private String customerName;
    private List<OrderItemRequest> items;
}
