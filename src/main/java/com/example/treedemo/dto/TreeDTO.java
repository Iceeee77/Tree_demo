package com.example.treedemo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TreeDTO {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String imageUrl;
    private List<String> adopters;
    private int adoptionCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastWateredTime;
} 