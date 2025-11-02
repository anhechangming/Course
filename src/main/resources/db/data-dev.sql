-- 注意：执行顺序需遵循“无依赖先执行”原则（学生表 → 课程表 → 选课表）
-- 避免外键关联失败（若后续添加外键约束，此顺序仍有效）

-- 1. 插入测试学生（3条数据，覆盖不同专业）
MERGE INTO students (id, student_id, name, major, grade, email, created_at) KEY(id)
    VALUES
-- 学生1：计算机科学专业
    ('1f2d3c4e-5a6b-7890-abcd-1234567890ab', 'S2024001', '张三', '计算机科学', 2024, 'zhangsan@zjgsu.edu.cn', NOW()),
-- 学生2：软件工程专业
    ('2f3d4c5e-6a7b-8901-bcde-234567890abc', 'S2024002', '李四', '软件工程', 2024, 'lisi@zjgsu.edu.cn', NOW()),
-- 学生3：人工智能专业
    ('3f4d5c6e-7a8b-9012-cdef-34567890abcd', 'S2024003', '王五', '人工智能', 2024, 'wangwu@zjgsu.edu.cn', NOW());

-- 2. 插入测试课程（3条数据，覆盖不同排课与讲师信息）
MERGE INTO courses (id, code, title, instructor_id, instructor_name, instructor_email,
    schedule_day_of_week, schedule_start_time, schedule_end_time, schedule_expected_attendance,
    capacity, enrolled, create_time) KEY(id)
    VALUES
-- 课程1：计算机科学导论
    ('4f5d6c7e-8a9b-0123-defa-4567890abcde', 'CS101', '计算机科学导论',
    'INS001', '王教授', 'wangjiao@zjgsu.edu.cn',
    'MONDAY', '09:00', '11:00', 40,
    50, 0, NOW()),
-- 课程2：高等数学
    ('5f6d7c8e-9a0b-1234-efab-567890abcdef', 'MA201', '高等数学',
    'INS002', '李教授', 'lijiao@zjgsu.edu.cn',
    'WEDNESDAY', '14:00', '16:00', 35,
    40, 0, NOW()),
-- 课程3：人工智能基础
    ('6f7d8c9e-0a1b-2345-fabc-67890abcdef0', 'AI301', '人工智能基础',
    'INS003', '张教授', 'zhangjiao@zjgsu.edu.cn',
    'FRIDAY', '10:00', '12:00', 30,
    35, 0, NOW());

-- 3. 插入测试选课记录（4条数据，覆盖正常选课、不同课程组合）
-- 使用 (course_id, student_id) 作为逻辑唯一键（因为一个学生不能重复选同一门课）
MERGE INTO enrollments (id, course_id, student_id, status, enroll_time) KEY(course_id, student_id)
    VALUES
-- 张三选 CS101
    ('7f8d9c0e-1a2b-3456-ghij-7890abcdef12', '4f5d6c7e-8a9b-0123-defa-4567890abcde', 'S2024001', 'ACTIVE', NOW()),
-- 张三选 AI301
    ('8f9da0be-2a3b-4567-hijk-8901abcdef23', '6f7d8c9e-0a1b-2345-fabc-67890abcdef0', 'S2024001', 'ACTIVE', NOW()),
-- 李四选 MA201
    ('9f0eb1cd-3a4b-5678-ijkl-9012abcdef34', '5f6d7c8e-9a0b-1234-efab-567890abcdef', 'S2024002', 'ACTIVE', NOW()),
-- 王五选 CS101
    ('0f1ec2df-4a5b-6789-jklm-0123abcdef45', '4f5d6c7e-8a9b-0123-defa-4567890abcde', 'S2024003', 'ACTIVE', NOW());