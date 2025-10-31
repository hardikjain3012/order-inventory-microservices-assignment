package com.korber.inventory.controller;

import com.korber.inventory.dto.BatchDto;
import com.korber.inventory.dto.InventoryUpdateRequest;
import com.korber.inventory.exceptions.InsufficientStockException;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<List<BatchDto>> getBatches(@PathVariable Long productId) {
        List<BatchDto> batches = inventoryService.getBatches(productId);
        return ResponseEntity.ok(batches);
    }


    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateInventory(@RequestBody InventoryUpdateRequest request) {
        inventoryService.updateInventory(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Inventory updated successfully"
        ));
    }
}
