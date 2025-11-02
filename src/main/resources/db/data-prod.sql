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
