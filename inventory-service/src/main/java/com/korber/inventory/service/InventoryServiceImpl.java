package com.korber.inventory.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.korber.inventory.exceptions.InsufficientStockException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.korber.inventory.dto.BatchDto;
import com.korber.inventory.dto.InventoryUpdateRequest;
import com.korber.inventory.entity.Batch;
import com.korber.inventory.entity.Product;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.repository.BatchRepository;
import com.korber.inventory.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private BatchRepository batchRepository;

    @Override
    @Transactional
    public List<BatchDto> getBatches(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));


        List<Batch> batches = batchRepository.findByProductOrderByExpiryDateAsc(product);


        return batches.stream()
                .map(b -> BatchDto.builder()
                        .batchId(b.getId())
                        .batchNumber(b.getBatchNumber())
                        .expiryDate(b.getExpiryDate())
                        .quantity(b.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(InventoryUpdateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + request.getProductId()));


        if (request.getAction() == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid inventory update request");
        }


        List<Batch> batches = batchRepository.findByProductOrderByExpiryDateAsc(product);


        switch (request.getAction().toUpperCase()) {
            case "DECREMENT" -> decrementStock(batches, request.getQuantity(), product);
            case "INCREMENT" -> incrementStock(batches, request.getQuantity(), product);
            default -> throw new IllegalArgumentException("Unsupported action: " + request.getAction());
        }
    }


    private void decrementStock(List<Batch> batches, int needed, Product product) {
        int totalAvailable = batches.stream().mapToInt(b -> b.getQuantity() != null ? b.getQuantity() : 0).sum();
        if (totalAvailable < needed) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        for (Batch b : batches) {
            int currentQty = b.getQuantity() == null ? 0 : b.getQuantity();
            if (currentQty == 0) continue;

            if (needed <= currentQty) {
                b.setQuantity(currentQty - needed);
                batchRepository.save(b);
                return;
            } else {
                needed -= currentQty;
                b.setQuantity(0);
                batchRepository.save(b);
            }
        }
    }


    private void incrementStock(List<Batch> batches, int quantity, Product product) {
        Batch targetBatch;
        if (batches.isEmpty()) {
            throw new NotFoundException("Product '" + product.getName() + "' has no batches to increment stock.");
        } else {
            targetBatch = batches.get(0);
            targetBatch.setQuantity(targetBatch.getQuantity() + quantity);
        }
        batchRepository.save(targetBatch);
    }

}
