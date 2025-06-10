package com.example.treedemo.controller;

import com.example.treedemo.dto.TreeDTO;
import com.example.treedemo.entity.Tree;
import com.example.treedemo.service.TreeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trees")
@CrossOrigin
public class TreeController {
    private static final Logger logger = LoggerFactory.getLogger(TreeController.class);

    @Autowired
    private TreeService treeService;

    @PostMapping
    public ResponseEntity<TreeDTO> createTree(@RequestBody Map<String, String> request) {
        try {
            Tree tree = treeService.createTree(
                    request.get("name"),
                    request.get("description"),
                    request.get("location"),
                    request.get("imageUrl")
            );
            return ResponseEntity.ok(convertToDTO(tree));
        } catch (Exception e) {
            logger.error("创建树木失败", e);
            throw e;
        }
    }

    @PostMapping("/{treeId}/adopt")
    public ResponseEntity<TreeDTO> adoptTree(
            @PathVariable Long treeId,
            @RequestBody Map<String, Long> request) {
        try {
            Tree tree = treeService.adoptTree(treeId, request.get("userId"));
            return ResponseEntity.ok(convertToDTO(tree));
        } catch (Exception e) {
            logger.error("认养树木失败", e);
            throw e;
        }
    }

    @PostMapping("/{treeId}/water")
    public ResponseEntity<TreeDTO> waterTree(
            @PathVariable Long treeId,
            @RequestBody Map<String, Long> request) {
        try {
            Tree tree = treeService.waterTree(treeId, request.get("userId"));
            return ResponseEntity.ok(convertToDTO(tree));
        } catch (Exception e) {
            logger.error("浇水失败", e);
            throw e;
        }
    }

    @GetMapping("/available")
    public ResponseEntity<List<TreeDTO>> getAvailableTrees() {
        try {
            logger.info("获取所有树木列表");
            List<Tree> trees = treeService.getAllTrees();
            List<TreeDTO> treeDTOs = new ArrayList<>();
            for (Tree tree : trees) {
                treeDTOs.add(convertToDTO(tree));
            }
            logger.info("成功获取到 {} 棵树", trees.size());
            return ResponseEntity.ok(treeDTOs);
        } catch (Exception e) {
            logger.error("获取树木列表失败", e);
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TreeDTO>> getUserTrees(@PathVariable Long userId) {
        try {
            List<Tree> trees = treeService.getUserTrees(userId);
            List<TreeDTO> treeDTOs = new ArrayList<>();
            for (Tree tree : trees) {
                treeDTOs.add(convertToDTO(tree));
            }
            return ResponseEntity.ok(treeDTOs);
        } catch (EntityNotFoundException e) {
            logger.error("获取用户树木失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreeDTO> getTreeById(@PathVariable Long id) {
        try {
            Tree tree = treeService.getTreeById(id);
            return ResponseEntity.ok(convertToDTO(tree));
        } catch (EntityNotFoundException e) {
            logger.error("获取树木详情失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{treeId}/adoption-count")
    public ResponseEntity<Integer> getAdoptionCount(@PathVariable Long treeId) {
        try {
            return ResponseEntity.ok(treeService.getAdoptionCount(treeId));
        } catch (EntityNotFoundException e) {
            logger.error("获取树木认养数量失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{treeId}/adopters")
    public ResponseEntity<List<String>> getAdopters(@PathVariable Long treeId) {
        try {
            return ResponseEntity.ok(treeService.getAdopters(treeId));
        } catch (EntityNotFoundException e) {
            logger.error("获取树木认养者列表失败: {}", e.getMessage());
            throw e;
        }
    }

    private TreeDTO convertToDTO(Tree tree) {
        TreeDTO dto = new TreeDTO();
        dto.setId(tree.getId());
        dto.setName(tree.getName());
        dto.setDescription(tree.getDescription());
        dto.setLocation(tree.getLocation());
        dto.setImageUrl(tree.getImageUrl());
        dto.setCreateTime(tree.getCreateTime());
        dto.setUpdateTime(tree.getUpdateTime());
        dto.setAdoptionCount(treeService.getAdoptionCount(tree.getId()));
        dto.setAdopters(treeService.getAdopters(tree.getId()));
        return dto;
    }
} 