package com.korber.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.korber.inventory.dto.BatchDto;
import com.korber.inventory.entity.Batch;
import com.korber.inventory.entity.Product;
import com.korber.inventory.exceptions.InventoryExceptionHandler;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.service.BatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BatchControllerTest {

    private MockMvc mvc;

    @Mock
    private BatchService batchService;

    @InjectMocks
    private BatchController batchController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper.registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mvc = MockMvcBuilders.standaloneSetup(batchController)
                .setControllerAdvice(new InventoryExceptionHandler())
                .build();
    }

    @Test
    void getShouldReturnDto() throws Exception {
        Product p = Product.builder().id(1L).name("P").sku("S").build();
        Batch b = Batch.builder().id(1L).product(p).batchNumber("B").expiryDate(LocalDate.now()).quantity(3).build();
        when(batchService.getById(1L)).thenReturn(b);

        mvc.perform(get("/batches/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").value(1));
    }

    @Test
    void getShouldReturn404WhenMissing() throws Exception {
        when(batchService.getById(999L)).thenThrow(new NotFoundException("Batch not found: 999"));

        mvc.perform(get("/batches/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        BatchDto dto = BatchDto.builder().productId(1L).batchNumber("N").expiryDate(LocalDate.now()).quantity(2).build();
        Product p = Product.builder().id(1L).name("P").sku("S").build();
        Batch created = Batch.builder().id(10L).product(p).batchNumber("N").expiryDate(LocalDate.now()).quantity(2).build();
        when(batchService.create(any())).thenReturn(created);

        mvc.perform(post("/batches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.batchId").value(10));
    }

    @Test
    void createShouldReturnBadRequestWhenInvalid() throws Exception {
        BatchDto dto = BatchDto.builder().productId(null).batchNumber(null).build();
        // service might throw IllegalArgumentException for invalid input
        when(batchService.create(any())).thenThrow(new IllegalArgumentException("Invalid batch"));

        mvc.perform(post("/batches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateShouldReturnOk() throws Exception {
        BatchDto dto = BatchDto.builder().productId(1L).batchNumber("N").expiryDate(LocalDate.now()).quantity(2).build();
        Product p = Product.builder().id(1L).name("P").sku("S").build();
        Batch updated = Batch.builder().id(11L).product(p).batchNumber("N").expiryDate(LocalDate.now()).quantity(5).build();
        when(batchService.update(eq(11L), any())).thenReturn(updated);

        mvc.perform(put("/batches/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").value(11))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void updateShouldReturn404WhenMissing() throws Exception {
        when(batchService.update(eq(20L), any())).thenThrow(new NotFoundException("Batch not found: 20"));

        mvc.perform(put("/batches/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BatchDto.builder().productId(1L).build())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteShouldReturnOk() throws Exception {
        mvc.perform(delete("/batches/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteShouldReturn404WhenMissing() throws Exception {
        doThrow(new NotFoundException("Batch not found: 30")).when(batchService).delete(30L);

        mvc.perform(delete("/batches/30"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
