package com.example.treedemo.service.impl;

import com.example.treedemo.entity.Tree;
import com.example.treedemo.entity.TreeAdoption;
import com.example.treedemo.entity.User;
import com.example.treedemo.repository.TreeAdoptionRepository;
import com.example.treedemo.repository.TreeRepository;
import com.example.treedemo.service.TreeService;
import com.example.treedemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TreeServiceImpl implements TreeService {

    @Autowired
    private TreeRepository treeRepository;

    @Autowired
    private TreeAdoptionRepository treeAdoptionRepository;

    @Autowired
    private UserService userService;

    @Override
    public Tree createTree(String name, String description, String location, String imageUrl) {
        Tree tree = new Tree();
        tree.setName(name);
        tree.setDescription(description);
        tree.setLocation(location);
        tree.setImageUrl(imageUrl);
        return treeRepository.save(tree);
    }

    @Override
    @Transactional
    public Tree adoptTree(Long treeId, Long userId) {
        Tree tree = getTreeById(treeId);
        User user = userService.getUserById(userId);

        // 检查用户是否已经认养过这棵树
        if (treeAdoptionRepository.findByTreeAndUser(tree, user).isPresent()) {
            throw new RuntimeException("您已经认养过这棵树了");
        }

        // 创建新的认养关系
        TreeAdoption adoption = new TreeAdoption();
        adoption.setTree(tree);
        adoption.setUser(user);
        treeAdoptionRepository.save(adoption);

        return tree;
    }

    @Override
    @Transactional
    public Tree waterTree(Long treeId, Long userId) {
        Tree tree = getTreeById(treeId);
        User user = userService.getUserById(userId);

        // 获取用户对这棵树的认养关系
        TreeAdoption adoption = treeAdoptionRepository.findByTreeAndUser(tree, user)
                .orElseThrow(() -> new RuntimeException("您还没有认养这棵树"));

        LocalDateTime now = LocalDateTime.now();
        if (adoption.getLastWateredTime() != null) {
            long hoursSinceLastWatered = ChronoUnit.HOURS.between(adoption.getLastWateredTime(), now);
            if (hoursSinceLastWatered < 24) {
                throw new RuntimeException("每棵树每天只能浇水一次");
            }
        }

        adoption.setLastWateredTime(now);
        treeAdoptionRepository.save(adoption);

        // Add points for watering
        userService.addPoints(userId, 10, "给" + tree.getName() + "浇水", "WATER");

        return tree;
    }

    @Override
    public List<Tree> getAvailableTrees() {
        return treeRepository.findAll();
    }

    @Override
    public List<Tree> getAllTrees() {
        return treeRepository.findAll();
    }

    @Override
    public List<Tree> getUserTrees(Long userId) {
        User user = userService.getUserById(userId);
        return treeAdoptionRepository.findByUser(user).stream()
                .map(TreeAdoption::getTree)
                .collect(Collectors.toList());
    }

    @Override
    public Tree getTreeById(Long id) {
        return treeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("未找到该树木"));
    }

    @Override
    public int getAdoptionCount(Long treeId) {
        Tree tree = getTreeById(treeId);
        return treeAdoptionRepository.findByTree(tree).size();
    }

    @Override
    public List<String> getAdopters(Long treeId) {
        Tree tree = getTreeById(treeId);
        return treeAdoptionRepository.findByTree(tree).stream()
                .map(adoption -> adoption.getUser().getUsername())
                .collect(Collectors.toList());
    }
} 