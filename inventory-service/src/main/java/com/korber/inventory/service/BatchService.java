package com.korber.inventory.service;

import java.util.List;

import com.korber.inventory.dto.BatchDto;
import com.korber.inventory.entity.Batch;

public interface BatchService {
    Batch create(BatchDto batchDto);
    Batch update(Long id, BatchDto batchDto);
    void delete(Long id);
    Batch getById(Long id);
}

