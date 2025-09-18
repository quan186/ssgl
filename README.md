# 学生宿舍管理系统

一个基于 Spring Boot 框架开发的现代化学生宿舍管理系统，提供完整的宿舍分配、维修申请和管理功能。

## 功能特性

### 🏠 宿舍管理
- 宿舍信息管理（宿舍楼、房间号、容量、类型等）
- 宿舍状态管理（可用、已满、维修中、关闭）
- 宿舍搜索和筛选功能
- 宿舍统计和入住率分析

### 👥 用户管理
- 学生和管理员账户管理
- 角色权限控制
- 个人信息管理
- 密码修改功能

### 🛏️ 宿舍分配
- 学生宿舍分配管理
- 入住和退宿记录
- 分配状态跟踪
- 分配统计和分析

### 🔧 维修管理
- 在线维修申请
- 维修类型和优先级管理
- 维修状态跟踪
- 维修统计和分析

### 📊 数据统计
- 宿舍入住率统计
- 维修申请统计
- 用户分布统计
- 可视化图表展示

## 技术栈

- **后端框架**: Spring Boot 3.2.0
- **数据库**: H2 Database (开发环境)
- **安全框架**: Spring Security 6
- **模板引擎**: Thymeleaf
- **前端框架**: Bootstrap 5.3.0
- **图标库**: Font Awesome 6.4.0
- **图表库**: Chart.js
- **构建工具**: Maven
- **Java版本**: 17

## 项目结构

```
src/
├── main/
│   ├── java/com/dormitory/
│   │   ├── config/          # 配置类
│   │   ├── controller/      # 控制器层
│   │   ├── entity/          # 实体类
│   │   ├── repository/      # 数据访问层
│   │   ├── service/         # 业务逻辑层
│   │   └── DormitoryManagementApplication.java
│   └── resources/
│       ├── static/          # 静态资源
│       │   ├── css/         # 样式文件
│       │   └── js/          # JavaScript文件
│       ├── templates/       # 模板文件
│       └── application.yml  # 配置文件
└── pom.xml                  # Maven配置
```

## 快速开始

### 环境要求
- JDK 17 或更高版本
- Maven 3.6 或更高版本

### 安装和运行

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd student-dormitory-management
   ```

2. **编译项目**
   ```bash
   mvn clean compile
   ```

3. **运行应用**
   ```bash
   mvn spring-boot:run
   ```

4. **访问应用**
   - 应用地址: http://localhost:8080
   - H2数据库控制台: http://localhost:8080/h2-console

### 测试账户

系统初始化时会自动创建以下测试账户：

#### 管理员账户
- 用户名: `admin`
- 密码: `admin123`
- 权限: 系统管理员

#### 学生账户
- 用户名: `student`
- 密码: `student123`
- 姓名: 张三

- 用户名: `student2`
- 密码: `student123`
- 姓名: 李四

- 用户名: `student3`
- 密码: `student123`
- 姓名: 王五

- 用户名: `student4`
- 密码: `student123`
- 姓名: 赵六

## 主要功能说明

### 管理员功能
1. **用户管理**: 创建、编辑、删除用户账户
2. **宿舍管理**: 管理宿舍信息，查看宿舍状态
3. **分配管理**: 为学生分配宿舍，处理退宿申请
4. **维修管理**: 处理维修申请，分配维修任务
5. **数据统计**: 查看各种统计数据和图表

### 学生功能
1. **查看宿舍**: 浏览可用宿舍信息
2. **我的宿舍**: 查看当前宿舍分配情况
3. **维修申请**: 提交维修申请，查看申请状态
4. **个人信息**: 管理个人资料和密码

## 数据库设计

### 主要实体
- **User**: 用户实体（学生和管理员）
- **Dormitory**: 宿舍实体
- **StudentDormitory**: 学生宿舍分配关联实体
- **RepairRequest**: 维修申请实体

### 关系设计
- 用户与宿舍分配：一对多关系
- 宿舍与分配记录：一对多关系
- 用户与维修申请：一对多关系
- 宿舍与维修申请：一对多关系

## 配置说明

### 数据库配置
默认使用H2内存数据库，配置在 `application.yml` 中：

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
```

### 安全配置
- 使用Spring Security进行身份认证和授权
- 支持基于角色的访问控制
- 密码使用BCrypt加密

## 开发指南

### 添加新功能
1. 在 `entity` 包中创建实体类
2. 在 `repository` 包中创建数据访问接口
3. 在 `service` 包中实现业务逻辑
4. 在 `controller` 包中创建控制器
5. 在 `templates` 中创建前端页面

### 代码规范
- 遵循Java命名规范
- 使用Spring Boot最佳实践
- 添加适当的注释和文档
- 进行单元测试

## 部署说明

### 生产环境部署
1. 修改数据库配置为生产数据库（如MySQL）
2. 配置应用服务器（如Tomcat）
3. 设置环境变量和配置文件
4. 构建WAR包并部署

### Docker部署
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/student-dormitory-management-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

如有问题或建议，请通过以下方式联系：
- 邮箱: [your-email@example.com]
- 项目地址: [repository-url]

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础的用户管理功能
- 实现宿舍管理功能
- 实现宿舍分配功能
- 实现维修申请功能
- 添加数据统计和图表展示
