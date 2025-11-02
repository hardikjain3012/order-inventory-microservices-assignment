package com.korber.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.korber.inventory.entity.Product;
import com.korber.inventory.exceptions.InventoryExceptionHandler;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    private MockMvc mvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // configure ObjectMapper to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new InventoryExceptionHandler())
                .build();
    }

    @Test
    void listShouldReturnProducts() throws Exception {
        Product p = Product.builder().id(1L).name("A").sku("S").build();
        when(productService.getAll()).thenReturn(List.of(p));

        mvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1));
    }

    @Test
    void getShouldReturnProduct() throws Exception {
        Product p = Product.builder().id(2L).name("B").sku("S2").build();
        when(productService.getById(2L)).thenReturn(p);

        mvc.perform(get("/products/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void getShouldReturn404WhenMissing() throws Exception {
        when(productService.getById(99L)).thenThrow(new NotFoundException("Product not found: 99"));

        mvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Product not found: 99"));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        Product p = Product.builder().id(3L).name("C").sku("S3").build();
        when(productService.create(any())).thenReturn(p);

        mvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void updateShouldReturnOk() throws Exception {
        Product p = Product.builder().id(4L).name("D").sku("S4").build();
        when(productService.update(eq(4L), any())).thenReturn(p);

        mvc.perform(put("/products/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    void updateShouldReturn404WhenMissing() throws Exception {
        when(productService.update(eq(50L), any())).thenThrow(new NotFoundException("Product not found: 50"));

        mvc.perform(put("/products/50")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Product.builder().name("X").sku("SX").build())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteShouldReturnOk() throws Exception {
        mvc.perform(delete("/products/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteShouldReturn404WhenMissing() throws Exception {
        doThrow(new NotFoundException("Product not found: 60")).when(productService).delete(60L);

        mvc.perform(delete("/products/60"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
