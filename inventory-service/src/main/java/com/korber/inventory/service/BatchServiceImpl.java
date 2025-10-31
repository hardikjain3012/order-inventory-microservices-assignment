package com.korber.inventory.service;

import java.util.List;

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
    public Batch create(Batch batch) {
        // verify product exists
        if (batch.getProduct() == null || batch.getProduct().getId() == null) {
            throw new IllegalArgumentException("Batch must reference an existing product id");
        }
        Product product = productRepository.findById(batch.getProduct().getId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + batch.getProduct().getId()));
        batch.setProduct(product);
        return batchRepository.save(batch);
    }

    @Override
    @Transactional
    public Batch update(Long id, Batch batch) {
        Batch existing = batchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Batch not found: " + id));
        existing.setBatchNumber(batch.getBatchNumber());
        existing.setExpiryDate(batch.getExpiryDate());
        existing.setQuantity(batch.getQuantity());
        // allow changing product by id
        if (batch.getProduct() != null && batch.getProduct().getId() != null) {
            Product p = productRepository.findById(batch.getProduct().getId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + batch.getProduct().getId()));
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

