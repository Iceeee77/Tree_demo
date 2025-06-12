# 树木认养与积分系统开发文档

## 1. 项目概述

### 1.1 项目背景
本项目是一个基于Spring Boot的树木认养与积分管理系统，旨在通过互联网技术促进环保意识，鼓励用户参与植树护绿活动。

### 1.2 技术栈
- 后端：Spring Boot 2.6.13
- 数据库：MySQL 8.0
- 前端：HTML5 + CSS3 + JavaScript
- 安全框架：Spring Security
- ORM框架：Spring Data JPA
- 其他工具：Lombok, JWT

## 2. 需求规格说明

### 2.1 功能需求

#### 2.1.1 用户管理模块
- 用户注册：支持用户名、密码、邮箱、手机号注册
- 用户登录：支持用户名密码登录
- 用户认证：基于JWT的token认证机制

#### 2.1.2 树木认养模块
- 树木列表：展示可认养的树木信息
- 认养功能：用户可以认养未被认养的树木
- 浇水功能：用户每天可以给认养的树木浇水一次
- 积分奖励：浇水操作可获得积分

#### 2.1.3 积分商城模块
- 商品展示：展示可兑换的商品列表
- 积分兑换：使用积分兑换商品
- 库存管理：自动更新商品库存

#### 2.1.4 社区互动模块
- 积分排行榜：展示用户积分排名
- 积分记录：记录用户积分变动历史

### 2.2 非功能需求
- 性能要求：页面响应时间<2s
- 安全要求：密码加密存储，防SQL注入
- 可用性要求：7*24小时运行
- 并发要求：支持100人同时在线

## 3. 系统设计

### 3.1 架构设计
采用经典三层架构：
- 表现层（Controller）：处理HTTP请求
- 业务层（Service）：实现业务逻辑
- 数据访问层（Repository）：操作数据库

### 3.2 数据库设计

#### 3.2.1 用户表（users）
\`\`\`sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    points INTEGER DEFAULT 0,
    create_time DATETIME,
    update_time DATETIME
);
\`\`\`

#### 3.2.2 树木表（trees）
\`\`\`sql
CREATE TABLE trees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    location VARCHAR(200),
    image_url VARCHAR(200),
    user_id BIGINT,
    last_watered_time DATETIME,
    create_time DATETIME,
    update_time DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
\`\`\`

#### 3.2.3 积分记录表（points_records）
\`\`\`sql
CREATE TABLE points_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    points INTEGER,
    description VARCHAR(200),
    type VARCHAR(50),
    create_time DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
\`\`\`

#### 3.2.4 商品表（products）
\`\`\`sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    points_required INTEGER,
    image_url VARCHAR(200),
    stock INTEGER,
    is_available BOOLEAN DEFAULT TRUE,
    create_time DATETIME,
    update_time DATETIME
);
\`\`\`

### 3.3 核心接口设计

#### 3.3.1 用户接口
- POST /api/users/register - 用户注册
- POST /api/users/login - 用户登录
- GET /api/users/top - 获取积分排行榜
- GET /api/users/{id} - 获取用户信息

#### 3.3.2 树木接口
- GET /api/trees/available - 获取可认养树木
- POST /api/trees/{treeId}/adopt - 认养树木
- POST /api/trees/{treeId}/water - 浇水
- GET /api/trees/user/{userId} - 获取用户的树木

#### 3.3.3 商品接口
- GET /api/products - 获取可用商品
- POST /api/products/{productId}/exchange - 兑换商品

## 4. 部署说明

### 4.1 环境要求
- JDK 1.8+
- MySQL 8.0+
- Maven 3.6+

### 4.2 配置说明
主要配置文件：src/main/resources/application.yml
\`\`\`yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tree_demo
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
\`\`\`

### 4.3 部署步骤
1. 创建数据库：
\`\`\`sql
CREATE DATABASE tree_demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
\`\`\`

2. 修改配置文件中的数据库连接信息

3. 构建项目：
\`\`\`bash
mvn clean install
\`\`\`

4. 运行项目：
\`\`\`bash
java -jar target/Tree-demo-0.0.1-SNAPSHOT.jar
\`\`\`

## 5. 代码结构说明

### 5.1 目录结构
\`\`\`
src/main/java/com/example/treedemo/
├── TreeDemoApplication.java    # 应用程序入口
├── config/                     # 配置类
├── controller/                 # 控制器
├── entity/                    # 实体类
├── repository/                # 数据访问层
└── service/                   # 服务层
    └── impl/                  # 服务实现
\`\`\`

### 5.2 核心类说明
- TreeDemoApplication: 应用程序入口，包含Spring Boot配置
- SecurityConfig: Spring Security配置，处理认证和授权
- UserController: 用户相关接口实现
- TreeController: 树木相关接口实现
- ProductController: 商品相关接口实现

## 6. 测试说明

### 6.1 测试环境搭建
1. 安装JDK 1.8+
2. 安装MySQL 8.0+
3. 创建测试数据库
4. 修改application.yml中的数据库配置

### 6.2 接口测试
可使用Postman进行接口测试：

1. 用户注册
\`\`\`http
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
    "username": "testuser",
    "password": "123456",
    "email": "test@example.com",
    "phone": "13800138000"
}
\`\`\`

2. 用户登录
\`\`\`http
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "123456"
}
\`\`\`

### 6.3 功能测试要点
1. 用户注册登录流程
2. 树木认养和浇水功能
3. 积分获取和消费流程
4. 商品兑换功能
5. 积分排行榜显示

## 7. 维护说明

### 7.1 日常维护
- 定期检查数据库备份
- 监控系统运行状态
- 检查日志文件大小

### 7.2 常见问题处理
1. 数据库连接失败
   - 检查数据库服务是否运行
   - 验证连接信息是否正确

2. 邮件服务异常
   - 检查邮件服务器配置
   - 验证邮箱授权码是否有效

3. 积分计算异常
   - 检查积分记录表
   - 验证积分变动逻辑

### 7.3 系统升级建议
1. 添加更多的用户互动功能
2. 优化积分计算规则
3. 增加管理员后台功能
4. 添加更多的数据统计分析 