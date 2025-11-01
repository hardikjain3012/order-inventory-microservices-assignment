package com.korber.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    @Qualifier("inventoryWebClient")
    private WebClient webClient;

    @Override
    public void decrementStock(Long productId, Integer quantity) {
        Map<String, Object> body = Map.of(
                "productId", productId,
                "quantity", quantity,
                "action", "DECREMENT"
        );

        webClient.post()
                .uri("/inventory/update")
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new RuntimeException("Inventory update failed for product: " + productId)))
                .bodyToMono(Void.class)
                .block();
    }
}
