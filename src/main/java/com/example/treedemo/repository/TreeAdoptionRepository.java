package com.example.treedemo.repository;

import com.example.treedemo.entity.Tree;
import com.example.treedemo.entity.TreeAdoption;
import com.example.treedemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreeAdoptionRepository extends JpaRepository<TreeAdoption, Long> {
    Optional<TreeAdoption> findByTreeAndUser(Tree tree, User user);
    List<TreeAdoption> findByUser(User user);
    List<TreeAdoption> findByTree(Tree tree);
} 