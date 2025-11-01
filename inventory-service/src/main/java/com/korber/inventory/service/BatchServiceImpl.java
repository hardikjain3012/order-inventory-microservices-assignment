package com.korber.inventory.service;

import java.util.List;

import com.korber.inventory.dto.BatchDto;
import com.korber.inventory.entity.Batch;
import com.korber.inventory.entity.Product;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.repository.BatchRepository;
import com.korber.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchServiceImpl implements BatchService {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Batch create(BatchDto batchDto) {
        // verify product exists
        if (batchDto.getProductId() == null) {
            throw new IllegalArgumentException("Batch must reference an existing product id");
        }
        Product product = productRepository.findById(batchDto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + batchDto.getProductId()));
        Batch batch = Batch.builder()
                .product(product)
                .batchNumber(batchDto.getBatchNumber())
                .expiryDate(batchDto.getExpiryDate())
                .quantity(batchDto.getQuantity())
                .build();
        return batchRepository.save(batch);
    }

    @Override
    @Transactional
    public Batch update(Long id, BatchDto batchDto) {
        Batch existing = batchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Batch not found: " + id));
        existing.setBatchNumber(batchDto.getBatchNumber());
        existing.setExpiryDate(batchDto.getExpiryDate());
        existing.setQuantity(batchDto.getQuantity());
        // allow changing product by id
        if (batchDto.getProductId() != null) {
            Product p = productRepository.findById(batchDto.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + batchDto.getProductId()));
            existing.setProduct(p);
        }
        return batchRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Batch existing = batchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Batch not found: " + id));
        batchRepository.delete(existing);
    }

    @Override
    public Batch getById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Batch not found: " + id));
    }
}

