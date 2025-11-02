# 校园选课系统（单体版）

## 项目简介
本项目是基于 Spring Boot 开发的校园选课系统，实现了课程管理、学生管理、选课管理三大核心功能，包含完整的业务逻辑校验（如课程时间冲突检查、选课容量限制、学生信息唯一性校验等），支持分页查询，提供标准 RESTful API 接口。

## 技术栈
- **框架**：Spring Boot 3.x
- **构建工具**：Maven
- **API 文档**：SpringDoc（Swagger）
- **数据存储**：（默认内存存储，可扩展为 MySQL 等关系型数据库）
- **开发语言**：Java 17+

## 环境要求
- JDK 17 及以上
- Maven 3.6 及以上
- （可选）MySQL 8.0（若使用数据库存储）

## 快速启动
**克隆仓库后直接在本地IDEA上运行**

```bash
git clone https://github.com/anhechangming/Course.cd Course
```

**访问地址**

- 项目默认端口：`8080`
- 基础 API 路径：`http://localhost:8080/api`
- Swagger 文档：`http://localhost:8080/swagger-ui/index.html`

## 核心功能模块

### 1. 课程管理

| 接口地址            | 请求方法 | 功能描述                                                     |
| ------------------- | -------- | ------------------------------------------------------------ |
| `/api/courses`      | GET      | 查询所有课程（按课程代码升序排列）                           |
| `/api/courses/{id}` | GET      | 根据 ID 查询课程详情                                         |
| `/api/courses`      | POST     | 创建课程（校验课程代码唯一性、课程时间冲突）                 |
| `/api/courses/{id}` | PUT      | 更新课程信息（禁止修改课程代码，校验时间冲突）               |
| `/api/courses/{id}` | DELETE   | 删除课程                                                     |
| `/api/courses/page` | GET      | 分页查询课程（支持参数：`pageNum` 页码，`pageSize` 每页条数） |

### 2. 学生管理

| 接口地址             | 请求方法 | 功能描述                                   |
| -------------------- | -------- | ------------------------------------------ |
| `/api/students`      | GET      | 查询所有学生                               |
| `/api/students/{id}` | GET      | 根据 ID 查询学生详情                       |
| `/api/students`      | POST     | 创建学生（校验学号唯一性、邮箱格式合法性） |
| `/api/students/{id}` | PUT      | 更新学生信息（如专业、邮箱）               |
| `/api/students/{id}` | DELETE   | 删除学生（若存在选课记录则禁止删除）       |

### 3. 选课管理

| 接口地址                               | 请求方法 | 功能描述                                              |
| -------------------------------------- | -------- | ----------------------------------------------------- |
| `/api/enrollments`                     | GET      | 查询所有选课记录                                      |
| `/api/enrollments`                     | POST     | 学生选课（校验课程容量、重复选课、学生 / 课程存在性） |
| `/api/enrollments/{id}`                | DELETE   | 学生退课                                              |
| `/api/enrollments/course/{courseId}`   | GET      | 根据课程 ID 查询选课记录                              |
| `/api/enrollments/student/{studentId}` | GET      | 根据学生 ID 查询选课记录                              |

## 关键业务规则

1. **课程时间冲突**：同一时间（星期几 + 时间段重叠）的课程无法创建。
2. **选课限制**：课程容量满后无法继续选课，学生不能重复选同一门课。
3. **数据校验**：学生学号、课程代码唯一；邮箱格式必须合法；课程容量为正数。
4. **分页逻辑**：分页查询默认从第 1 页开始，支持自定义每页条数。

## 测试说明

1. **测试工具**：推荐使用 Apifox 或 Postman 导入 HTTP 测试用例进行验证。
2. **测试场景**：包含课程时间冲突、选课容量限制、分页查询、学生删除校验等核心场景（测试用例可参考项目导出的 HTTP 文件）。

## 项目结构

```plaintext
src/
├── main/
│   ├── java/com/zjgsu/cyd/course/
│   │   ├── controller/      // 接口控制器（CourseController、StudentController等）
│   │   ├── service/         // 业务逻辑层
│   │   ├── model/           // 实体类（Course、Student、Enrollment等）
│   │   ├── repository/      // 数据访问层
│   │   ├── DTO/             // 数据传输对象（如PageQueryDTO）
│   │   ├── Response/        // 统一响应结果（Result）
│   │   └── Config/          // 配置类（如SpringDocConfig）
│   └── resources/           // 配置文件
└── test/                    // 测试代码
```
