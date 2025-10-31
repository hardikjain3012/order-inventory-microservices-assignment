package com.korber.inventory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryUpdateRequest {
	private Long productId;
	private Integer quantity;
	private String action;
}
