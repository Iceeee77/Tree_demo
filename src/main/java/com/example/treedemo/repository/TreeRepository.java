package com.example.treedemo.repository;

import com.example.treedemo.entity.Tree;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeRepository extends JpaRepository<Tree, Long> {
} 