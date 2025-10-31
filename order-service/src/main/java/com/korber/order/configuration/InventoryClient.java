package com.korber.order.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class InventoryClient {

    @Value("${inventory.service.base-url}")
    private String baseUrl;

    @Bean
    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
