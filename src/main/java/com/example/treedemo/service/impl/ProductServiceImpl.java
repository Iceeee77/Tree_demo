package com.example.treedemo.service.impl;

import com.example.treedemo.entity.Product;
import com.example.treedemo.repository.ProductRepository;
import com.example.treedemo.service.ProductService;
import com.example.treedemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Override
    public Product createProduct(String name, String description, Integer pointsRequired, String imageUrl, Integer stock) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPointsRequired(pointsRequired);
        product.setImageUrl(imageUrl);
        product.setStock(stock);
        product.setIsAvailable(true);
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @Override
    public List<Product> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue();
    }

    @Override
    @Transactional
    public void exchangeProduct(Long productId, Long userId) {
        Product product = getProductById(productId);
        
        if (!product.getIsAvailable()) {
            throw new RuntimeException("Product is not available");
        }
        
        if (product.getStock() <= 0) {
            throw new RuntimeException("Product is out of stock");
        }

        // Deduct points from user
        userService.deductPoints(userId, product.getPointsRequired(), 
                "Exchanged product: " + product.getName(), "EXCHANGE");

        // Update product stock
        product.setStock(product.getStock() - 1);
        if (product.getStock() == 0) {
            product.setIsAvailable(false);
        }
        productRepository.save(product);
    }
} 