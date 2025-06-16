package com.example.treedemo.service;

import com.example.treedemo.entity.User;

import java.util.List;

public interface UserService {
    User register(String username, String password, String email, String phone);
    User login(String username, String password);
    User getUserById(Long id);
    User getUserByUsername(String username);
    List<User> getTopUsers();
    void addPoints(Long userId, int points, String description, String type);
    void deductPoints(Long userId, int points, String description, String type);
} 