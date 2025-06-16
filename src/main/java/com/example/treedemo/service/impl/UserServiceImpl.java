package com.example.treedemo.service.impl;

import com.example.treedemo.entity.PointsRecord;
import com.example.treedemo.entity.User;
import com.example.treedemo.repository.PointsRecordRepository;
import com.example.treedemo.repository.UserRepository;
import com.example.treedemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsRecordRepository pointsRecordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(String username, String password, String email, String phone) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Phone already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhone(phone);
        user.setPoints(0);

        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public List<User> getTopUsers() {
        return userRepository.findTopUsersByPoints();
    }

    @Override
    @Transactional
    public void addPoints(Long userId, int points, String description, String type) {
        User user = getUserById(userId);
        user.setPoints(user.getPoints() + points);
        userRepository.save(user);

        PointsRecord record = new PointsRecord();
        record.setUser(user);
        record.setPoints(points);
        record.setDescription(description);
        record.setType(type);
        pointsRecordRepository.save(record);
    }

    @Override
    @Transactional
    public void deductPoints(Long userId, int points, String description, String type) {
        User user = getUserById(userId);
        if (user.getPoints() < points) {
            throw new RuntimeException("积分不足");
        }
        user.setPoints(user.getPoints() - points);
        userRepository.save(user);

        PointsRecord record = new PointsRecord();
        record.setUser(user);
        record.setPoints(-points);
        record.setDescription(description);
        record.setType(type);
        pointsRecordRepository.save(record);
    }
} 