package com.example.treedemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-connection")
    public Map<String, Object> testDatabaseConnection() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 执行一个简单的SQL查询
            String dbVersion = jdbcTemplate.queryForObject("SELECT version()", String.class);
            result.put("status", "success");
            result.put("message", "数据库连接成功");
            result.put("dbVersion", dbVersion);
            result.put("timestamp", System.currentTimeMillis());
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "数据库连接失败: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }
        return result;
    }

    @GetMapping("/update-points")
    public Map<String, Object> updateUserPoints() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 更新大蛋用户的积分
            int updatedRows = jdbcTemplate.update(
                "UPDATE users SET points = 150 WHERE username = '大蛋'"
            );
            result.put("status", "success");
            result.put("message", "积分更新成功");
            result.put("updatedRows", updatedRows);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "积分更新失败: " + e.getMessage());
        }
        return result;
    }
} 