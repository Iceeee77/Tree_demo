package com.example.treedemo.controller;

import com.example.treedemo.entity.User;
import com.example.treedemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody Map<String, String> request) {
        User user = userService.register(
                request.get("username"),
                request.get("password"),
                request.get("email"),
                request.get("phone")
        );
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody Map<String, String> request) {
        User user = userService.login(
                request.get("username"),
                request.get("password")
        );
        return ResponseEntity.ok(user);
    }

    @GetMapping("/top")
    public ResponseEntity<List<User>> getTopUsers() {
        return ResponseEntity.ok(userService.getTopUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
} 