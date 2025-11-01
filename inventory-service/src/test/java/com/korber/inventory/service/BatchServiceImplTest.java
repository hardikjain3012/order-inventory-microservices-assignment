package com.korber.inventory.service;

import com.korber.inventory.dto.BatchDto;
import com.korber.inventory.entity.Batch;
import com.korber.inventory.entity.Product;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.repository.BatchRepository;
import com.korber.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BatchServiceImplTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BatchServiceImpl batchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createShouldThrowWhenNoProductId() {
        BatchDto dto = BatchDto.builder().build();
        assertThatThrownBy(() -> batchService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reference an existing product id");
    }

    @Test
    void createShouldThrowWhenProductMissing() {
        BatchDto dto = BatchDto.builder().productId(10L).build();
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> batchService.create(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found: 10");
    }

    @Test
    void createShouldSaveWhenValid() {
        Product p = Product.builder().id(1L).name("P").sku("S").build();
        BatchDto dto = BatchDto.builder().productId(1L).batchNumber("B1").expiryDate(LocalDate.now()).quantity(5).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(batchRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Batch created = batchService.create(dto);

        assertThat(created.getProduct()).isEqualTo(p);
        assertThat(created.getBatchNumber()).isEqualTo("B1");
        verify(batchRepository).save(any(Batch.class));
    }

    @Test
    void updateShouldThrowWhenBatchMissing() {
        when(batchRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> batchService.update(2L, BatchDto.builder().build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Batch not found: 2");
    }

    @Test
    void updateShouldThrowWhenProductChangeInvalid() {
        Product p = Product.builder().id(1L).name("P").sku("S").build();
        Batch existing = Batch.builder().id(3L).product(p).batchNumber("X").expiryDate(LocalDate.now()).quantity(2).build();
        when(batchRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        BatchDto dto = BatchDto.builder().productId(99L).batchNumber("Y").expiryDate(LocalDate.now()).quantity(7).build();

        assertThatThrownBy(() -> batchService.update(3L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found: 99");
    }

    @Test
    void updateShouldSaveWhenValid() {
        Product p = Product.builder().id(1L).name("P").sku("S").build();
        Product newP = Product.builder().id(2L).name("NP").sku("S2").build();
        Batch existing = Batch.builder().id(4L).product(p).batchNumber("X").expiryDate(LocalDate.now()).quantity(2).build();
        when(batchRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(productRepository.findById(2L)).thenReturn(Optional.of(newP));
        when(batchRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BatchDto dto = BatchDto.builder().productId(2L).batchNumber("Z").expiryDate(LocalDate.now()).quantity(9).build();

        Batch updated = batchService.update(4L, dto);

        assertThat(updated.getBatchNumber()).isEqualTo("Z");
        assertThat(updated.getQuantity()).isEqualTo(9);
        assertThat(updated.getProduct()).isEqualTo(newP);
    }

    @Test
    void deleteShouldThrowWhenMissing() {
        when(batchRepository.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> batchService.delete(5L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Batch not found: 5");
    }

    @Test
    void deleteShouldCallRepositoryWhenExists() {
        Product p = Product.builder().id(6L).name("P").sku("S").build();
        Batch existing = Batch.builder().id(6L).product(p).batchNumber("N").expiryDate(LocalDate.now()).quantity(1).build();
        when(batchRepository.findById(6L)).thenReturn(Optional.of(existing));

        batchService.delete(6L);

        verify(batchRepository).delete(existing);
    }

    @Test
    void getByIdShouldThrowWhenMissing() {
        when(batchRepository.findById(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> batchService.getById(7L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Batch not found: 7");
    }

    @Test
    void getByIdShouldReturnWhenFound() {
        Product p = Product.builder().id(8L).name("P").sku("S").build();
        Batch b = Batch.builder().id(8L).product(p).batchNumber("N").expiryDate(LocalDate.now()).quantity(3).build();
        when(batchRepository.findById(8L)).thenReturn(Optional.of(b));

        Batch got = batchService.getById(8L);

        assertThat(got).isEqualTo(b);
    }
}

