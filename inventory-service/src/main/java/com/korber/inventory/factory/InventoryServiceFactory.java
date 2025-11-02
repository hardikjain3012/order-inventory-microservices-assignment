package com.korber.inventory.factory;

import com.korber.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class InventoryServiceFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public InventoryService getService(String strategy) {
        if (strategy == null || strategy.isBlank()) {
            strategy = "FEFOInventoryService"; // default
        }
        if (applicationContext.containsBean(strategy)) {
            return (InventoryService) applicationContext.getBean(strategy);
        }
        throw new IllegalArgumentException("Unknown inventory strategy: " + strategy);
    }
}
