package com.korber.order.service;

import com.korber.order.dto.OrderItemRequest;
import com.korber.order.dto.OrderRequest;
import com.korber.order.dto.OrderResponse;
import com.korber.order.entity.Order;
import com.korber.order.entity.OrderItem;
import com.korber.order.exceptions.OrderProcessingException;
import com.korber.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void placeOrderShouldCallInventoryAndSave() {
        OrderItemRequest item = OrderItemRequest.builder().productId(1L).quantity(2).build();
        OrderRequest req = OrderRequest.builder().customerName("Alice").items(List.of(item)).build();

        when(orderRepository.save(any())).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(123L);
            if (saved.getOrderDate() == null) saved.setOrderDate(LocalDateTime.now());
            return saved;
        });

        OrderResponse resp = orderService.placeOrder(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getOrderId()).isEqualTo(123L);
        assertThat(resp.getStatus()).isEqualTo("PLACED");
        assertThat(resp.getItems()).hasSize(1);
    }

    @Test
    void placeOrderShouldWrapInventoryExceptions() {
        OrderItemRequest item = OrderItemRequest.builder().productId(2L).quantity(5).build();
        OrderRequest req = OrderRequest.builder().customerName("Bob").items(List.of(item)).build();

        doThrow(new RuntimeException("inventory down")).when(inventoryService).decrementStock(2L, 5);

        assertThatThrownBy(() -> orderService.placeOrder(req))
                .isInstanceOf(OrderProcessingException.class)
                .hasMessageContaining("Failed to place order");
    }
}

