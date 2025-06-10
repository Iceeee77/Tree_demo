package com.example.treedemo.config;

import com.example.treedemo.entity.Tree;
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

    @Override
    @Transactional
    public void run(String... args) {
        // 只有当没有树木数据时才初始化
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
    }
} 