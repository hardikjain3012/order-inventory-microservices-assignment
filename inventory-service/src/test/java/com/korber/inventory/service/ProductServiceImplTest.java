package com.korber.inventory.service;

import com.korber.inventory.entity.Product;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createShouldSaveAndReturn() {
        Product p = Product.builder().id(1L).name("N").sku("SKU1").build();
        when(productRepository.save(p)).thenReturn(p);

        Product result = productService.create(p);

        assertThat(result).isEqualTo(p);
        verify(productRepository).save(p);
    }

    @Test
    void updateShouldModifyWhenExists() {
        Product existing = Product.builder().id(1L).name("Old").sku("S1").build();
        Product incoming = Product.builder().name("New").sku("S2").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Product updated = productService.update(1L, incoming);

        assertThat(updated.getName()).isEqualTo("New");
        assertThat(updated.getSku()).isEqualTo("S2");
        verify(productRepository).findById(1L);
        verify(productRepository).save(existing);
    }

    @Test
    void updateShouldThrowNotFoundWhenMissing() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(2L, new Product()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found: 2");
    }

    @Test
    void deleteShouldRemoveWhenExists() {
        Product existing = Product.builder().id(3L).name("X").sku("S3").build();
        when(productRepository.findById(3L)).thenReturn(Optional.of(existing));

        productService.delete(3L);

        verify(productRepository).delete(existing);
    }

    @Test
    void deleteShouldThrowNotFoundWhenMissing() {
        when(productRepository.findById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(4L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found: 4");
    }

    @Test
    void getByIdShouldReturnWhenExists() {
        Product existing = Product.builder().id(5L).name("Y").sku("S5").build();
        when(productRepository.findById(5L)).thenReturn(Optional.of(existing));

        Product got = productService.getById(5L);

        assertThat(got).isEqualTo(existing);
    }

    @Test
    void getByIdShouldThrowWhenMissing() {
        when(productRepository.findById(6L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(6L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found: 6");
    }

    @Test
    void getAllShouldReturnList() {
        Product p1 = Product.builder().id(7L).name("A").sku("S7").build();
        when(productRepository.findAll()).thenReturn(List.of(p1));

        List<Product> list = productService.getAll();

        assertThat(list).hasSize(1).contains(p1);
    }
}
