package com.example.treedemo.service;

import com.example.treedemo.entity.Tree;
import java.util.List;

public interface TreeService {
    Tree createTree(String name, String description, String location, String imageUrl);
    Tree adoptTree(Long treeId, Long userId);
    Tree waterTree(Long treeId, Long userId);
    List<Tree> getAvailableTrees();
    List<Tree> getAllTrees();
    List<Tree> getUserTrees(Long userId);
    Tree getTreeById(Long id);
    int getAdoptionCount(Long treeId);
    List<String> getAdopters(Long treeId);
} 