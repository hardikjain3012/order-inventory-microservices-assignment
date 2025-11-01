package com.korber.inventory.service;

import com.korber.inventory.dto.BatchDto;
import com.korber.inventory.dto.InventoryUpdateRequest;
import com.korber.inventory.entity.Batch;
import com.korber.inventory.entity.Product;
import com.korber.inventory.exceptions.InsufficientStockException;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.repository.BatchRepository;
import com.korber.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BatchRepository batchRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBatchesShouldThrowWhenProductMissing() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> inventoryService.getBatches(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found: 1");
    }

    @Test
    void getBatchesShouldReturnDtos() {
        Product p = Product.builder().id(2L).name("P").sku("S").build();
        Batch b1 = Batch.builder().id(10L).product(p).batchNumber("B1").expiryDate(LocalDate.now()).quantity(5).build();
        when(productRepository.findById(2L)).thenReturn(Optional.of(p));
        when(batchRepository.findByProductOrderByExpiryDateAsc(p)).thenReturn(List.of(b1));

        var dtos = inventoryService.getBatches(2L);

        assertThat(dtos).hasSize(1);
        BatchDto dto = dtos.get(0);
        assertThat(dto.getBatchId()).isEqualTo(10L);
        assertThat(dto.getQuantity()).isEqualTo(5);
    }

    @Test
    void updateInventoryShouldThrowWhenProductMissing() {
        InventoryUpdateRequest req = InventoryUpdateRequest.builder().productId(3L).build();
        req.setAction("DECREMENT");
        req.setQuantity(1);
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.updateInventory(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found: 3");
    }

    @Test
    void updateInventoryShouldThrowOnInvalidRequest() {
        Product p = Product.builder().id(4L).name("P").sku("S").build();
        InventoryUpdateRequest req = InventoryUpdateRequest.builder().productId(4L).build();
        // missing action and quantity invalid
        when(productRepository.findById(4L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> inventoryService.updateInventory(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid inventory update request");
    }

    @Test
    void updateInventoryShouldDecrementAcrossBatches() {
        Product p = Product.builder().id(5L).name("P").sku("S").build();
        Batch b1 = Batch.builder().id(1L).product(p).batchNumber("A").expiryDate(LocalDate.now()).quantity(3).build();
        Batch b2 = Batch.builder().id(2L).product(p).batchNumber("B").expiryDate(LocalDate.now().plusDays(1)).quantity(4).build();
        when(productRepository.findById(5L)).thenReturn(Optional.of(p));
        when(batchRepository.findByProductOrderByExpiryDateAsc(p)).thenReturn(List.of(b1, b2));
        when(batchRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        InventoryUpdateRequest req = InventoryUpdateRequest.builder().productId(5L).action("DECREMENT").quantity(5).build();

        inventoryService.updateInventory(req);

        // after decrement 5: b1 -> 0, b2 -> 2
        assertThat(b1.getQuantity()).isEqualTo(0);
        assertThat(b2.getQuantity()).isEqualTo(2);
        verify(batchRepository, atLeast(1)).save(any(Batch.class));
    }

    @Test
    void updateInventoryShouldThrowInsufficientWhenNotEnough() {
        Product p = Product.builder().id(6L).name("P").sku("S").build();
        Batch b1 = Batch.builder().id(1L).product(p).batchNumber("A").expiryDate(LocalDate.now()).quantity(2).build();
        when(productRepository.findById(6L)).thenReturn(Optional.of(p));
        when(batchRepository.findByProductOrderByExpiryDateAsc(p)).thenReturn(List.of(b1));

        InventoryUpdateRequest req = InventoryUpdateRequest.builder().productId(6L).action("DECREMENT").quantity(5).build();

        assertThatThrownBy(() -> inventoryService.updateInventory(req))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void updateInventoryShouldIncrementWhenBatchesPresent() {
        Product p = Product.builder().id(7L).name("P").sku("S").build();
        Batch b1 = Batch.builder().id(1L).product(p).batchNumber("A").expiryDate(LocalDate.now()).quantity(2).build();
        when(productRepository.findById(7L)).thenReturn(Optional.of(p));
        when(batchRepository.findByProductOrderByExpiryDateAsc(p)).thenReturn(List.of(b1));
        when(batchRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        InventoryUpdateRequest req = InventoryUpdateRequest.builder().productId(7L).action("INCREMENT").quantity(5).build();

        inventoryService.updateInventory(req);

        assertThat(b1.getQuantity()).isEqualTo(7);
    }

    @Test
    void updateInventoryShouldThrowWhenIncrementNoBatches() {
        Product p = Product.builder().id(8L).name("P").sku("S").build();
        when(productRepository.findById(8L)).thenReturn(Optional.of(p));
        when(batchRepository.findByProductOrderByExpiryDateAsc(p)).thenReturn(List.of());

        InventoryUpdateRequest req = InventoryUpdateRequest.builder().productId(8L).action("INCREMENT").quantity(5).build();

        assertThatThrownBy(() -> inventoryService.updateInventory(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("has no batches to increment");
    }
}
