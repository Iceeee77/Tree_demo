package com.example.treedemo.controller;

import com.example.treedemo.entity.Product;
import com.example.treedemo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Map<String, Object> request) {
        Product product = productService.createProduct(
                (String) request.get("name"),
                (String) request.get("description"),
                (Integer) request.get("pointsRequired"),
                (String) request.get("imageUrl"),
                (Integer) request.get("stock")
        );
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAvailableProducts() {
        return ResponseEntity.ok(productService.getAvailableProducts());
    }

    @PostMapping("/{productId}/exchange")
    public ResponseEntity<?> exchangeProduct(
            @PathVariable Long productId,
            @RequestParam Long userId) {
        productService.exchangeProduct(productId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
} 