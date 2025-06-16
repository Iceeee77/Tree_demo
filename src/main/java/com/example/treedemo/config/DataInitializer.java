package com.example.treedemo.config;

import com.example.treedemo.entity.Product;
import com.example.treedemo.entity.Tree;
import com.example.treedemo.repository.ProductRepository;
import com.example.treedemo.repository.TreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TreeRepository treeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // 初始化树木数据
        if (treeRepository.count() == 0) {
            List<Tree> trees = new ArrayList<>();
            
            // 银杏树
            Tree ginkgo = new Tree();
            ginkgo.setName("银杏树");
            ginkgo.setDescription("银杏树是世界上最古老的树种之一，被誉为\"活化石\"。这棵银杏树已有50年树龄，树冠优美，秋季叶片金黄，非常适合认养。");
            ginkgo.setLocation("校园中心广场");
            trees.add(ginkgo);

            // 樱花树
            Tree cherry = new Tree();
            cherry.setName("樱花树");
            cherry.setDescription("这是一棵美丽的樱花树，每年春季都会绽放粉色花朵，为校园增添浪漫气息。适合喜欢樱花的认养者。");
            cherry.setLocation("图书馆前");
            trees.add(cherry);

            // 香樟树
            Tree camphor = new Tree();
            camphor.setName("香樟树");
            camphor.setDescription("香樟树四季常青，树形优美，散发淡淡香气。这棵香樟树已生长20年，枝繁叶茂。");
            camphor.setLocation("教学楼前");
            trees.add(camphor);

            // 松树
            Tree pine = new Tree();
            pine.setName("松树");
            pine.setDescription("这是一棵挺拔的松树，象征着坚韧和长寿。松树常绿不凋，是优秀的园林树种。");
            pine.setLocation("体育场旁");
            trees.add(pine);

            // 梅花树
            Tree plum = new Tree();
            plum.setName("梅花树");
            plum.setDescription("梅花是我国传统名花，象征着坚强和高洁。这棵梅花树在寒冬绽放，为校园增添一抹靓丽。");
            plum.setLocation("艺术楼前");
            trees.add(plum);
            
            treeRepository.saveAll(trees);
        }

        // 初始化商品数据
        if (productRepository.count() == 0) {
            List<Product> products = new ArrayList<>();

            // 环保购物袋
            Product bag = new Product();
            bag.setName("环保购物袋");
            bag.setDescription("可重复使用的环保购物袋，采用环保材料制作，结实耐用。");
            bag.setPointsRequired(100);
            bag.setStock(50);
            bag.setIsAvailable(true);
            products.add(bag);

            // 植物种子套装
            Product seeds = new Product();
            seeds.setName("植物种子套装");
            seeds.setDescription("包含多种植物种子，适合在家种植，美化环境。");
            seeds.setPointsRequired(200);
            seeds.setStock(30);
            seeds.setIsAvailable(true);
            products.add(seeds);

            // 环保水杯
            Product cup = new Product();
            cup.setName("环保水杯");
            cup.setDescription("不锈钢保温杯，可重复使用，减少一次性塑料杯的使用。");
            cup.setPointsRequired(300);
            cup.setStock(20);
            cup.setIsAvailable(true);
            products.add(cup);

            // 有机肥料
            Product fertilizer = new Product();
            fertilizer.setName("有机肥料");
            fertilizer.setDescription("天然有机肥料，适合家庭种植使用，环保无污染。");
            fertilizer.setPointsRequired(150);
            fertilizer.setStock(40);
            fertilizer.setIsAvailable(true);
            products.add(fertilizer);

            productRepository.saveAll(products);
        }
    }
} 