package com.example.treedemo.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "points_records")
public class PointsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer points;  // 积分变动数量（正数为增加，负数为减少）
    
    private String description;  // 积分变动描述（例如："浇水获得积分"，"兑换商品扣除积分"）
    
    private String type;  // 积分变动类型（WATER-浇水，EXCHANGE-兑换等）

    @CreationTimestamp
    private LocalDateTime createTime;  // 记录创建时间
} 