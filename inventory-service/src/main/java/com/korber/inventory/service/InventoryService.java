package com.korber.inventory.service;

import java.util.List;

import com.korber.inventory.dto.BatchDto;
import com.korber.inventory.dto.InventoryUpdateRequest;

public interface InventoryService {
	
	List<BatchDto> getBatches(Long productId);
	void updateInventory(InventoryUpdateRequest request);

}
