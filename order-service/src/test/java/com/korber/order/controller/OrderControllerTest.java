package com.korber.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.korber.order.dto.OrderItemRequest;
import com.korber.order.dto.OrderRequest;
import com.korber.order.dto.OrderResponse;
import com.korber.order.exceptions.OrderExceptionHandler;
import com.korber.order.exceptions.OrderProcessingException;
import com.korber.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest {

    private MockMvc mvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // configure ObjectMapper to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new OrderExceptionHandler())
                .build();
    }

    @Test
    void createOrderShouldReturnCreated() throws Exception {
        OrderItemRequest i1 = OrderItemRequest.builder().productId(1L).quantity(2).build();
        OrderRequest req = OrderRequest.builder().customerName("C").items(List.of(i1)).build();

        OrderResponse resp = OrderResponse.builder().orderId(10L).orderDate(LocalDateTime.now()).status("PLACED").customerName("C").items(req.getItems()).build();
        when(orderService.placeOrder(any())).thenReturn(resp);

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(10));
    }

    @Test
    void createOrderShouldReturnServerErrorWhenProcessingFails() throws Exception {
        OrderItemRequest i1 = OrderItemRequest.builder().productId(1L).quantity(2).build();
        OrderRequest req = OrderRequest.builder().customerName("C").items(List.of(i1)).build();

        doThrow(new OrderProcessingException("failed")).when(orderService).placeOrder(any());

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }
}
