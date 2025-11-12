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

# 校园选课系统 - Docker 部署指南

## 前提条件

- 已安装 Docker Engine 和 Docker Compose
- 配置镜像加速（可选，提升构建速度）
- 克隆项目代码后，进入项目根目录（`/home/admin01/桌面/05b`）

## 核心文件说明

| 文件名称                 | 作用描述                                    |
| ------------------------ | ------------------------------------------- |
| `Dockerfile`             | 多阶段构建应用镜像，优化镜像大小与运行安全  |
| `.dockerignore`          | 排除构建无关文件，减少镜像体积              |
| `docker-compose.yml`     | 编排应用与 MySQL 服务，配置网络和数据持久化 |
| `application-docker.yml` | Docker 环境专用配置，适配容器化部署         |

## 部署步骤

### 1. 构建镜像

```bash
# 基于 Dockerfile 构建应用镜像，自动拉取依赖镜像
docker compose build
```

### 2. 启动服务

```bash
# 后台启动应用和 MySQL 服务，自动创建网络和数据卷
docker compose up -d
```

### 3. 验证服务状态

```bash
# 查看容器运行状态（确保 app 和 mysql 均为 Up/Healthy）
docker compose ps

# 查看应用启动日志（验证 profile 激活、数据库连接成功）
docker compose logs -f app
```

### 4. 功能测试

#### （1）访问应用接口

在浏览器或终端执行以下命令，验证应用可用性：

```bash
curl http://localhost:8080/api/courses
```

- 预期结果：返回 JSON 格式课程列表（空列表或已有数据）

#### （2）新增测试课程

```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "code":"HW001",
    "title":"作业测试课程",
    "capacity":20,
    "schedule":{
      "dayOfWeek":"MONDAY",
      "startTime":"09:00",
      "endTime":"11:00"
    },
    "instructorId":"INS001",
    "instructorName":"张老师",
    "instructorEmail":"zhang@example.com"
  }'
```

#### （3）数据持久化验证

```bash
# 停止服务（保留数据卷）
docker compose down

# 重新启动服务
docker compose up -d

# 验证数据未丢失
curl http://localhost:8080/api/courses | grep "HW001"
```

## 常用操作命令

### 停止服务

```bash
# 停止容器，不删除数据卷和镜像（推荐）
docker compose down

# 强制停止容器（服务卡死时使用）
docker compose kill
```

### 日志查看

```bash
# 实时查看应用日志
docker compose logs -f app

# 查看 MySQL 日志（排查数据库问题）
docker compose logs -f mysql
```

### 资源清理

```bash
# 清理 Docker 临时资源（停止的容器、缓存等）
docker system prune -f

# 清理镜像构建缓存
docker builder prune -f
```

## 常见问题排查

1. **应用启动失败，提示无法连接数据库**
   - 解决方案：执行 `docker compose ps` 确认 MySQL 容器状态为 `Healthy`；查看 MySQL 日志 `docker compose logs mysql` 排查数据库启动问题。
2. **新增课程返回 404 错误**
   - 解决方案：检查请求 JSON 是否包含必填字段（如 `schedule`、讲师信息），参考 “新增测试课程” 命令补全参数。
3. **镜像大小超过 200MB**
   - 解决方案：确认 `Dockerfile` 使用多阶段构建；检查 `.dockerignore` 是否排除 `target/`、`.git/` 等无用文件。
4. **数据重启后丢失**
   - 解决方案：确保 `docker-compose.yml` 中 MySQL 配置了命名卷挂载（`- mysql_data:/var/lib/mysql`）；避免使用 `docker compose down -v`（会删除数据卷）。
