package com.example.inventory.service;

import com.example.inventory.dto.OrderItemRequest;
import com.example.inventory.model.Product;
import com.example.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryValidationService {

    private final ProductRepository productRepository;

    public InventoryValidationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public boolean isInventoryAvailable(List<OrderItemRequest> items) {
        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.productId()).orElse(null);
            if (product == null || product.getStockQuantity() < item.quantity()) {
                return false;
            }
        }
        return true;
    }
}
