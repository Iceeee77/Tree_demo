package com.example.treedemo.service.impl;

import com.example.treedemo.entity.Product;
import com.example.treedemo.entity.User;
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
                .orElseThrow(() -> new EntityNotFoundException("商品不存在"));
    }

    @Override
    public List<Product> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue();
    }

    @Override
    @Transactional
    public Product exchangeProduct(Long productId, Long userId) {
        Product product = getProductById(productId);
        User user = userService.getUserById(userId);

        if (!product.getIsAvailable()) {
            throw new RuntimeException("商品已下架");
        }

        if (product.getStock() <= 0) {
            throw new RuntimeException("商品库存不足");
        }

        if (user.getPoints() < product.getPointsRequired()) {
            throw new RuntimeException("积分不足");
        }

        // 扣除积分
        userService.deductPoints(userId, product.getPointsRequired(), "兑换商品：" + product.getName(), "EXCHANGE");
        
        // 减少库存
        product.setStock(product.getStock() - 1);
        if (product.getStock() == 0) {
            product.setIsAvailable(false);
        }
        
        return productRepository.save(product);
    }
} 