package com.korber.inventory.service;

import java.util.List;

import com.korber.inventory.entity.Batch;

public interface BatchService {
    Batch create(Batch batch);
    Batch update(Long id, Batch batch);
    void delete(Long id);
    Batch getById(Long id);
}

