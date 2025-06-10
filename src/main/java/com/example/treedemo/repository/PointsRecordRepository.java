package com.example.treedemo.repository;

import com.example.treedemo.entity.PointsRecord;
import com.example.treedemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointsRecordRepository extends JpaRepository<PointsRecord, Long> {
    List<PointsRecord> findByUserOrderByCreateTimeDesc(User user);
} 