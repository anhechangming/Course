# 校园选课系统数据库初始化脚本与使用说明

本文档对应 `homework2.pdf` 任务五 “数据初始化与迁移” 要求，提供 H2 开发环境与 MySQL 生产环境的初始化脚本及使用步骤，确保脚本执行符合 “开发环境自动执行、生产环境手动导入” 的文档规范

| 环境           | 脚本文件名        | 作用                    | 存放路径                 | 对应文档要求         |
| -------------- | ----------------- | ----------------------- | ------------------------ | -------------------- |
| H2 开发环境    | `schema.sql`      | 创建数据库表结构        | `src/main/resources/db/` | （建表语句）         |
| H2 开发环境    | `data-dev.sql`    | 插入开发环境测试数据    | `src/main/resources/db/` | （基础测试数据）     |
| MySQL 生产环境 | `schema-prod.sql` | 创建 MySQL 兼容的表结构 | `src/main/resources/db/` | （生产环境脚本）     |
| MySQL 生产环境 | `data-prod.sql`   | 插入生产环境基础数据    | `src/main/resources/db/` | （生产环境基线数据） |

## 二、H2 开发环境脚本（自动执行）

### 1. `schema.sql`（建表脚本）

```sql
-- 1. 学生表：对应 Student 实体，学号/邮箱唯一，自动维护创建时间
CREATE TABLE IF NOT EXISTS students (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    major VARCHAR(50) NOT NULL,
    grade INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL,
    -- 唯一约束（学号与邮箱唯一）
    CONSTRAINT uk_student_studentid UNIQUE (student_id),
    CONSTRAINT uk_student_email UNIQUE (email),
    -- 索引（支持按专业/年级筛选）
    KEY idx_student_major (major),
    KEY idx_student_grade (grade)
);

-- 2. 课程表：对应 Course 实体，课程代码唯一，含嵌入式讲师/排课信息
CREATE TABLE IF NOT EXISTS courses (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    title VARCHAR(100) NOT NULL,
    -- 嵌入式 Instructor 字段
    instructor_id VARCHAR(20) NOT NULL,
    instructor_name VARCHAR(50) NOT NULL,
    instructor_email VARCHAR(100) NOT NULL,
    -- 嵌入式 ScheduleSlot 字段
    schedule_dayOfWeek VARCHAR(10) NOT NULL,
    schedule_startTime VARCHAR(5) NOT NULL,
    schedule_endTime VARCHAR(5) NOT NULL,
    schedule_expectedAttendance INT NOT NULL,
    -- 课程核心字段
    capacity INT NOT NULL,
    enrolled INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL,
    -- 唯一约束
    CONSTRAINT uk_course_code UNIQUE (code),
    -- 索引
    KEY idx_course_instructor (instructor_id),
    KEY idx_course_schedule (schedule_dayOfWeek, schedule_startTime)
);

-- 3. 选课表：对应 Enrollment 实体，课程+学生双重唯一
CREATE TABLE IF NOT EXISTS enrollments (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    course_id VARCHAR(36) NOT NULL,
    student_id VARCHAR(20) NOT NULL,
    -- 枚举类型：选课状态
    status VARCHAR(20) NOT NULL,
    enroll_time DATETIME NOT NULL,
    -- 唯一约束
    CONSTRAINT uk_enrollment_course_student UNIQUE (course_id, student_id),
    -- 索引
    KEY idx_enrollment_course (course_id),
    KEY idx_enrollment_student (student_id),
    KEY idx_enrollment_course_status (course_id, status)
);
```

### 2. `data-dev.sql`（测试数据脚本）

```sql
-- 注意：执行顺序需与表创建顺序一致（students → courses → enrollments）
-- 1. 插入学生测试数据（3条，覆盖不同专业）
INSERT INTO students (id, student_id, name, major, grade, email, created_at)
VALUES 
('1f2d3c4e-5a6b-7890-abcd-1234567890ab', 'S2024001', '张三', '计算机科学', 2024, 'zhangsan@zjgsu.edu.cn', NOW()),
('2f3d4c5e-6a7b-8901-bcde-234567890abc', 'S2024002', '李四', '软件工程', 2024, 'lisi@zjgsu.edu.cn', NOW()),
('3f4d5c6e-7a8b-9012-cdef-34567890abcd', 'S2024003', '王五', '人工智能', 2024, 'wangwu@zjgsu.edu.cn', NOW());

-- 2. 插入课程测试数据（3条，含嵌入式讲师/排课信息）
INSERT INTO courses (id, code, title, instructor_id, instructor_name, instructor_email, 
                     schedule_dayOfWeek, schedule_startTime, schedule_endTime, schedule_expectedAttendance,
                     capacity, enrolled, create_time)
VALUES 
('4f5d6c7e-8a9b-0123-defa-4567890abcde', 'CS101', '计算机科学导论', 
 'INS001', '王教授', 'wangjiao@zjgsu.edu.cn',
 'MONDAY', '09:00', '11:00', 40,  -- 排课信息（星期/时间/预期出勤）
 50, 0, NOW()),  -- 容量50，初始已选0人
('5f6d7c8e-9a0b-1234-efab-567890abcdef', 'MA201', '高等数学', 
 'INS002', '李教授', 'lijiao@zjgsu.edu.cn',
 'WEDNESDAY', '14:00', '16:00', 35,
 40, 0, NOW()),
('6f7d8c9e-0a1b-2345-fabc-67890abcdef0', 'AI301', '人工智能基础', 
 'INS003', '张教授', 'zhangjiao@zjgsu.edu.cn',
 'FRIDAY', '10:00', '12:00', 30,
 35, 0, NOW());

-- 3. 插入选课测试数据（4条，覆盖正常选课场景）
INSERT INTO enrollments (id, course_id, student_id, status, enroll_time)
VALUES 
('7f8d9c0e-1a2b-3456-ghij-7890abcdef12', '4f5d6c7e-8a9b-0123-defa-4567890abcde', 'S2024001', 'ACTIVE', NOW()),
('8f9da0be-2a3b-4567-hijk-8901abcdef23', '6f7d8c9e-0a1b-2345-fabc-67890abcdef0', 'S2024001', 'ACTIVE', NOW()),
('9f0eb1cd-3a4b-5678-ijkl-9012abcdef34', '5f6d7c8e-9a0b-1234-efab-567890abcdef', 'S2024002', 'ACTIVE', NOW()),
('0f1ec2df-4a5b-6789-jklm-0123abcdef45', '4f5d6c7e-8a9b-0123-defa-4567890abcde', 'S2024003', 'ACTIVE', NOW());
```

### 3. H2 环境脚本使用步骤（自动执行）

按 `homework2.pdf` 任务五要求（🔶1-72），H2 开发环境无需手动执行脚本，配置后启动应用自动执行：

1. 配置 `application-dev.yml`

   ：确保脚本路径与执行模式正确（🔶1-35、🔶1-38）：

   ```yaml
   spring:
     datasource:
       url: jdbc:h2:file:./data/course_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
       driverClassName: org.h2.Driver
       username: sa
       password: ''
     h2:
       console:
         enabled: true  # 启用 H2 控制台
     jpa:
       hibernate:
         ddl-auto: validate  # 仅校验表结构，不自动修改
       properties:
         hibernate:
           format_sql: true  # 格式化 SQL 日志
       show-sql: true  # 显示执行的 SQL 语句
     # 配置 SQL 脚本执行（Spring Boot 原生支持，优先级高于 INIT=RUNSCRIPT）
     sql:
       init:
         mode: always  # 每次启动都执行脚本
         schema-locations: classpath:db/schema.sql  # 先执行建表脚本
         data-locations: classpath:db/data-dev.sql  # 后执行插入数据脚本
         encoding: UTF-8  # 脚本编码
   ```

   配置 `application.yml`

   ```yaml
   # application.yml（全局配置，所有环境通用）
   spring:
     # 关键：指定默认激活的环境为 dev，启动时自动加载 application-dev.yml
     profiles:
       active: dev
     # 其他全局配置（如JPA通用参数，可覆盖环境配置）
     jpa:
       open-in-view: false  # 关闭OpenSessionInView，所有环境通
   ```

2. 启动应用

   ：直接启动 Spring Boot 应用，日志会显示脚本执行成功：

   - 建表日志：`Executing SQL script from class path resource [db/schema.sql]`
   - 数据日志：`Executing SQL script from class path resource [db/data-dev.sql]`

3. **验证数据**：访问 H2 控制台（`http://localhost:8080/h2-console`），执行 `SELECT * FROM students;` 等语句，确认数据插入正常（🔶1-91）。

## 三、MySQL 生产环境脚本（手动导入）

### 1. `schema-prod.sql`（MySQL 兼容建表脚本）

```sql
-- ============================================
--  学生信息管理系统 数据库结构（可重复执行版）
-- ============================================

-- 1. 学生表（students）
CREATE TABLE IF NOT EXISTS students (
                                        id VARCHAR(36) NOT NULL PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    major VARCHAR(50) NOT NULL,
    grade INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL
    );

-- 2. 课程表（courses）
CREATE TABLE IF NOT EXISTS courses (
                                       id VARCHAR(36) NOT NULL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    title VARCHAR(100) NOT NULL,
    instructor_id VARCHAR(20) NOT NULL,
    instructor_name VARCHAR(50) NOT NULL,
    instructor_email VARCHAR(100) NOT NULL,
    schedule_day_of_week VARCHAR(10) NOT NULL,
    schedule_start_time VARCHAR(5) NOT NULL,
    schedule_end_time VARCHAR(5) NOT NULL,
    schedule_expected_attendance INT NOT NULL,
    capacity INT NOT NULL,
    enrolled INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL
    );

-- 3. 选课表（enrollments）
CREATE TABLE IF NOT EXISTS enrollments (
                                           id VARCHAR(36) NOT NULL PRIMARY KEY,
    course_id VARCHAR(36) NOT NULL,
    student_id VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    enroll_time DATETIME NOT NULL
    );

-- ==================================================
--  动态检测索引或唯一约束是否存在再创建（幂等执行）
-- ==================================================
DELIMITER //

CREATE PROCEDURE create_index_if_not_exists(
    IN tbl_name VARCHAR(64),
    IN idx_name VARCHAR(64),
    IN idx_sql  TEXT
)
BEGIN
  DECLARE idx_count INT DEFAULT 0;
SELECT COUNT(*) INTO idx_count
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = tbl_name
  AND INDEX_NAME = idx_name;

IF idx_count = 0 THEN
    SET @s = idx_sql;
PREPARE ps FROM @s;
EXECUTE ps;
DEALLOCATE PREPARE ps;
END IF;
END;
//

DELIMITER ;

-- ============================================
-- students 表的唯一约束与索引
-- ============================================

CALL create_index_if_not_exists('students', 'uk_student_studentid',
  'CREATE UNIQUE INDEX uk_student_studentid ON students (student_id)');
CALL create_index_if_not_exists('students', 'uk_student_email',
  'CREATE UNIQUE INDEX uk_student_email ON students (email)');

CALL create_index_if_not_exists('students', 'idx_student_major',
  'CREATE INDEX idx_student_major ON students (major)');
CALL create_index_if_not_exists('students', 'idx_student_grade',
  'CREATE INDEX idx_student_grade ON students (grade)');

-- ============================================
-- courses 表的唯一约束与索引
-- ============================================

CALL create_index_if_not_exists('courses', 'uk_course_code',
  'CREATE UNIQUE INDEX uk_course_code ON courses (code)');
CALL create_index_if_not_exists('courses', 'idx_course_instructor',
  'CREATE INDEX idx_course_instructor ON courses (instructor_id)');
CALL create_index_if_not_exists('courses', 'idx_course_day_of_week',
  'CREATE INDEX idx_course_day_of_week ON courses (schedule_day_of_week)');

-- ============================================
-- enrollments 表的唯一约束与索引
-- ============================================

CALL create_index_if_not_exists('enrollments', 'uk_enrollment_course_student',
  'CREATE UNIQUE INDEX uk_enrollment_course_student ON enrollments (course_id, student_id)');
CALL create_index_if_not_exists('enrollments', 'idx_enrollment_course',
  'CREATE INDEX idx_enrollment_course ON enrollments (course_id)');
CALL create_index_if_not_exists('enrollments', 'idx_enrollment_student',
  'CREATE INDEX idx_enrollment_student ON enrollments (student_id)');
CALL create_index_if_not_exists('enrollments', 'idx_enrollment_status',
  'CREATE INDEX idx_enrollment_status ON enrollments (status)');

-- ============================================
-- 清理过程（避免污染命名空间）
-- ============================================
DROP PROCEDURE IF EXISTS create_index_if_not_exists;

-- ============================================
-- ✅ 脚本执行完成
-- ============================================
SELECT '✅ 所有表与索引已安全创建完毕（幂等可重复执行）' AS result;

```

### 2. `data-prod.sql`（生产环境基础数据脚本）

```sql
-- ===========================
-- 测试数据插入（可重复执行）
-- 顺序：students -> courses -> enrollments
-- 使用 MySQL 的 upsert：INSERT ... ON DUPLICATE KEY UPDATE
-- ===========================

-- 1) 插入/更新 students（3 条）
INSERT INTO students (id, student_id, name, major, grade, email, created_at)
VALUES
    ('1f2d3c4e-5a6b-7890-abcd-1234567890ab', 'S2024001', '张三', '计算机科学', 2024, 'zhangsan@zjgsu.edu.cn', NOW()),
    ('2f3d4c5e-6a7b-8901-bcde-234567890abc', 'S2024002', '李四', '软件工程', 2024, 'lisi@zjgsu.edu.cn', NOW()),
    ('3f4d5c6e-7a8b-9012-cdef-34567890abcd', 'S2024003', '王五', '人工智能', 2024, 'wangwu@zjgsu.edu.cn', NOW())
    ON DUPLICATE KEY UPDATE
                         student_id = VALUES(student_id),
                         name = VALUES(name),
                         major = VALUES(major),
                         grade = VALUES(grade),
                         email = VALUES(email),
                         created_at = VALUES(created_at);


-- 2) 插入/更新 courses（3 条）
INSERT INTO courses (id, code, title, instructor_id, instructor_name, instructor_email,
                     schedule_day_of_week, schedule_start_time, schedule_end_time, schedule_expected_attendance,
                     capacity, enrolled, create_time)
VALUES
    ('4f5d6c7e-8a9b-0123-defa-4567890abcde', 'CS101', '计算机科学导论',
     'INS001', '王教授', 'wangjiao@zjgsu.edu.cn',
     'MONDAY', '09:00', '11:00', 40,
     50, 0, NOW()),
    ('5f6d7c8e-9a0b-1234-efab-567890abcdef', 'MA201', '高等数学',
     'INS002', '李教授', 'lijiao@zjgsu.edu.cn',
     'WEDNESDAY', '14:00', '16:00', 35,
     40, 0, NOW()),
    ('6f7d8c9e-0a1b-2345-fabc-67890abcdef0', 'AI301', '人工智能基础',
     'INS003', '张教授', 'zhangjiao@zjgsu.edu.cn',
     'FRIDAY', '10:00', '12:00', 30,
     35, 0, NOW())
    ON DUPLICATE KEY UPDATE
                         code = VALUES(code),
                         title = VALUES(title),
                         instructor_id = VALUES(instructor_id),
                         instructor_name = VALUES(instructor_name),
                         instructor_email = VALUES(instructor_email),
                         schedule_day_of_week = VALUES(schedule_day_of_week),
                         schedule_start_time = VALUES(schedule_start_time),
                         schedule_end_time = VALUES(schedule_end_time),
                         schedule_expected_attendance = VALUES(schedule_expected_attendance),
                         capacity = VALUES(capacity),
                         enrolled = VALUES(enrolled),
                         create_time = VALUES(create_time);


-- 3) 插入/更新 enrollments（4 条）
-- 注意：enrollments 有唯一索引 uk_enrollment_course_student(course_id, student_id)
-- 我们在冲突时只更新 status 与 enroll_time，而不覆盖原有 id（保持原 id 不变）
INSERT INTO enrollments (id, course_id, student_id, status, enroll_time)
VALUES
    ('7f8d9c0e-1a2b-3456-ghij-7890abcdef12', '4f5d6c7e-8a9b-0123-defa-4567890abcde', 'S2024001', 'ACTIVE', NOW()),
    ('8f9da0be-2a3b-4567-hijk-8901abcdef23', '6f7d8c9e-0a1b-2345-fabc-67890abcdef0', 'S2024001', 'ACTIVE', NOW()),
    ('9f0eb1cd-3a4b-5678-ijkl-9012abcdef34', '5f6d7c8e-9a0b-1234-efab-567890abcdef', 'S2024002', 'ACTIVE', NOW()),
    ('0f1ec2df-4a5b-6789-jklm-0123abcdef45', '4f5d6c7e-8a9b-0123-defa-4567890abcde', 'S2024003', 'ACTIVE', NOW())
    ON DUPLICATE KEY UPDATE
                         -- 不修改 id（保留已有 id），只更新状态与时间
                         status = VALUES(status),
                         enroll_time = VALUES(enroll_time);

```

### 3. MySQL 环境脚本使用步骤（手动执行）

按 `homework2.pdf` 任务五要求（🔶1-72），生产环境需手动导入脚本，避免自动执行风险：

1. 准备 MySQL 环境

   - 登录 MySQL 客户端，创建数据库（🔶1-25）：

     ```sql
     CREATE DATABASE course_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
     USE course_db;
     ```

2. 导入建表脚本

   ：在终端执行命令（非 MySQL 客户端内）：

   ```bash
   # 格式：mysql -u 用户名 -p 数据库名 < 脚本路径
   mysql -u course_user -p course_db < src/main/resources/db/schema-prod.sql
   ```

   执行后输入密码，确认无报错（可登录 MySQL 客户端执行验证表创建成功）。

   ```
   SHOW TABLES;
   ```

3. 导入基础数据

   ：执行数据脚本：

   ```bash
   mysql -u course_user -p course_db < src/main/resources/db/data-prod.sql
   ```

   

4. 配置应用并启动

   ：修改application-prod.yml

   ```yaml
   spring:
     datasource:
       # 关键修正：删除 characterEncoding=utf8mb4，保留&useSSL=false&serverTimezone=Asia/Shanghai serverTimezone 等必要参数
       url: jdbc:mysql://localhost:3306/course_db
       driverClassName: com.mysql.cj.jdbc.Driver  # MySQL 8.0+ 必须用 cj 驱动
       username: root # 替换为你的 MySQL 用户名（如之前创建的 course_user）
       password: 123456  # 替换为你的 MySQL 密码
       hikari:  # 按文档要求配置 Hikari 连接池（🔶1-77）
         maximum-pool-size: 10
         minimum-idle: 5
         idle-timeout: 300000
         connection-timeout: 20000
     jpa:
       hibernate:
         ddl-auto: validate  # 生产环境仅校验表结构（文档要求🔶1-38）
       properties:
         hibernate:
           dialect: org.hibernate.dialect.MySQL8Dialect  # 明确 MySQL 8 方言
           show-sql: false  # 生产环境关闭 SQL 日志（文档要求🔶1-38）
     sql:
       init:
         mode: never  # 生产环境禁止自动执行脚本（文档要求🔶1-72）
   ```

   配置 application.yml

   ```yaml
   # application.yml（全局配置，所有环境通用）
   spring:
     profiles:
       active: prod
     # 其他全局配置（如JPA通用参数，可覆盖环境配置）
     jpa:
       open-in-view: false  # 关闭OpenSessionInView，所有环境通用
   ```

   启动应用，通过健康检查接口（

   ```
   curl localhost:8080/health/db
   ```

   ）验证数据库连接正常（🔶1-78）。

## 四、脚本维护与注意事项

1. **版本一致性**：确保脚本与实体类字段映射一致（如 `schedule_dayOfWeek` 列名需与 `ScheduleSlot` 实体的 `@Column(name)` 完全匹配，🔶1-43）；
2. **生产环境安全**：`data-prod.sql` 避免插入测试数据，密码等敏感信息需通过环境变量注入（而非硬编码，🔶1-77）；
3. **脚本更新记录**：每次修改脚本需记录更新内容（如新增字段、调整索引），便于后续迁移（🔶1-73）；
4. **兼容性检查**：H2 脚本与 MySQL 脚本需区分语法差异（如 H2 支持 `KEY` 定义索引，MySQL 需显式指定 `INDEX` 或 `KEY`，且需加 `ENGINE=InnoDB`）。

本文档完全遵循 `homework2.pdf` 中 “数据初始化与迁移”“配置与部署验证” 的要求，可直接作为作业提交材料中的 “初始化脚本与使用说明”（🔶1-96）。