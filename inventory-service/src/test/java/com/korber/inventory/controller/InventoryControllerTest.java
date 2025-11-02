package com.korber.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.korber.inventory.dto.BatchDto;
import com.korber.inventory.dto.InventoryUpdateRequest;
import com.korber.inventory.exceptions.InventoryExceptionHandler;
import com.korber.inventory.exceptions.NotFoundException;
import com.korber.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryControllerTest {

    private MockMvc mvc;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // configure ObjectMapper to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mvc = MockMvcBuilders.standaloneSetup(inventoryController)
                .setControllerAdvice(new InventoryExceptionHandler())
                .build();
    }

    @Test
    void getBatchesShouldReturnList() throws Exception {
        BatchDto dto = BatchDto.builder().productId(1L).batchId(2L).batchNumber("B").expiryDate(LocalDate.now()).quantity(3).build();
        when(inventoryService.getBatches(1L)).thenReturn(List.of(dto));

        mvc.perform(get("/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].batchId").value(2));
    }

    @Test
    void getBatchesShouldReturn404WhenProductMissing() throws Exception {
        when(inventoryService.getBatches(100L)).thenThrow(new NotFoundException("Product not found: 100"));

        mvc.perform(get("/inventory/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateInventoryShouldReturnOk() throws Exception {
        InventoryUpdateRequest req = InventoryUpdateRequest.builder().productId(1L).action("DECREMENT").quantity(2).build();

        mvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateInventoryShouldReturnBadRequestWhenInvalid() throws Exception {
        InventoryUpdateRequest req = InventoryUpdateRequest.builder().productId(1L).build();
        doThrow(new IllegalArgumentException("Invalid inventory update request")).when(inventoryService).updateInventory(any());

        mvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
