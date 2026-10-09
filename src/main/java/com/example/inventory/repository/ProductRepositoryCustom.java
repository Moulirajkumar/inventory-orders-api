package com.example.inventory.repository;

import com.example.inventory.dto.ProductSearchCriteria;
import com.example.inventory.model.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> searchProducts(ProductSearchCriteria criteria);
}
