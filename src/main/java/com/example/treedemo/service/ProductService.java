package com.example.treedemo.service;

import com.example.treedemo.entity.Product;
import java.util.List;

public interface ProductService {
    Product createProduct(String name, String description, Integer pointsRequired, String imageUrl, Integer stock);
    Product getProductById(Long id);
    List<Product> getAvailableProducts();
    void exchangeProduct(Long productId, Long userId);
} 