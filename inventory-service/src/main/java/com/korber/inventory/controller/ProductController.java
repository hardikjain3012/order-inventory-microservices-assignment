package com.korber.inventory.controller;

import java.util.List;
import java.util.Map;

import com.korber.inventory.dto.ProductDto;
import com.korber.inventory.entity.Product;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> list() {
        List<ProductDto> products = productService.getAll().stream()
                .map(p -> ProductDto.builder()
                        .productId(p.getId())
                        .name(p.getName())
                        .sku(p.getSku())
                        .build())
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> get(@PathVariable Long id) {
        Product product = productService.getById(id);
        ProductDto dto = ProductDto.builder()
                .productId(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody Product product) {
        product = productService.create(product);
        ProductDto dto = ProductDto.builder()
                .productId(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody Product product) {
        product = productService.update(id, product);
        ProductDto dto = ProductDto.builder()
                .productId(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .build();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}

