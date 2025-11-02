package com.korber.inventory.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.korber.inventory.dto.BatchDto;
import com.korber.inventory.entity.Batch;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batches")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @GetMapping("/{id}")
    public ResponseEntity<BatchDto> get(@PathVariable Long id) {
        Batch b = batchService.getById(id);
        BatchDto dto = BatchDto.builder()
                .productId(id)
                .batchId(b.getId())
                .batchNumber(b.getBatchNumber())
                .expiryDate(b.getExpiryDate())
                .quantity(b.getQuantity())
                .build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<BatchDto> create(@RequestBody BatchDto batchDto) {
        Batch created = batchService.create(batchDto);
        BatchDto dto = BatchDto.builder()
                .productId(batchDto.getProductId())
                .batchId(created.getId())
                .batchNumber(created.getBatchNumber())
                .expiryDate(created.getExpiryDate())
                .quantity(created.getQuantity())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchDto> update(@PathVariable Long id, @RequestBody BatchDto batchDto) {
        Batch updated = batchService.update(id, batchDto);
        BatchDto dto = BatchDto.builder()
                .productId(batchDto.getProductId())
                .batchId(updated.getId())
                .batchNumber(updated.getBatchNumber())
                .expiryDate(updated.getExpiryDate())
                .quantity(updated.getQuantity())
                .build();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        batchService.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}

