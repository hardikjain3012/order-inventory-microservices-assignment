package com.korber.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec uriSpec;

    @Mock
    private WebClient.RequestBodySpec bodySpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq("/inventory/update"))).thenReturn(bodySpec);
    }

    @Test
    void decrementStockShouldSucceedWhenRemoteReturns2xx() {
        // setup chain: bodyValue -> headersSpec, retrieve -> responseSpec, bodyToMono(Void) -> Mono.empty()
        doReturn(headersSpec).when(bodySpec).bodyValue(any());
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        inventoryService.decrementStock(1L, 2);

        verify(webClient).post();
        verify(responseSpec).bodyToMono(Void.class);
    }

    @Test
    void decrementStockShouldThrowWhenRemoteFails() {
        doReturn(headersSpec).when(bodySpec).bodyValue(any());
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        doReturn(responseSpec).when(responseSpec).onStatus(any(), any());
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new RuntimeException("Inventory update failed")));

        assertThatThrownBy(() -> inventoryService.decrementStock(2L, 3))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Inventory update failed");
    }
}
