# 校园选课系统（单体版）

## 项目简介
基于Spring Boot开发的校园选课系统，实现课程管理、学生管理、选课管理核心功能，符合作业要求的业务规则和接口规范。

## 环境要求
- JDK 17+
- Spring Boot 3.2.x
- Maven 3.6+

## 启动步骤
1. 克隆仓库：`git clone https://github.com/anhechangming/Course.git`
2. 进入项目目录：`cd Course`
3. 编译项目：`mvn clean package`
4. 运行jar包：`java -jar target/course-system-0.0.1-SNAPSHOT.jar`
5. 访问接口：`http://localhost:8080/api/courses`
6. 访问Swagger文档：`http://localhost:8080/swagger-ui.html`

## 核心功能
- 课程管理：创建、查询、更新、删除课程
- 学生管理：创建、查询、更新、删除学生
- 选课管理：学生选课、退课、查询选课记录，包含容量限制和重复选课校验
