-- 1. 学生表（students）：仅定义列和主键，约束与索引表外创建
CREATE TABLE IF NOT EXISTS students (
                                        id VARCHAR(36) NOT NULL PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    major VARCHAR(50) NOT NULL,
    grade INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL
    );

-- 学生表唯一约束（表外创建）
ALTER TABLE students ADD CONSTRAINT IF NOT EXISTS uk_student_studentid UNIQUE (student_id);
ALTER TABLE students ADD CONSTRAINT IF NOT EXISTS uk_student_email UNIQUE (email);

-- 学生表索引（表外创建）
CREATE INDEX IF NOT EXISTS idx_student_major ON students (major);
CREATE INDEX IF NOT EXISTS idx_student_grade ON students (grade);


-- 2. 课程表（courses）：表定义 + 表外约束与索引
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

-- 课程表唯一约束
ALTER TABLE courses ADD CONSTRAINT IF NOT EXISTS uk_course_code UNIQUE (code);

-- 课程表索引
CREATE INDEX IF NOT EXISTS idx_course_instructor ON courses (instructor_id);
CREATE INDEX IF NOT EXISTS idx_course_day_Of_Week ON courses (schedule_day_Of_Week);


-- 3. 选课表（enrollments）：表定义 + 表外约束与索引
CREATE TABLE IF NOT EXISTS enrollments (
                                           id VARCHAR(36) NOT NULL PRIMARY KEY,
    course_id VARCHAR(36) NOT NULL,
    student_id VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    enroll_time DATETIME NOT NULL
    );

-- 选课表唯一约束（避免重复选课）
ALTER TABLE enrollments ADD CONSTRAINT IF NOT EXISTS uk_enrollment_course_student UNIQUE (course_id, student_id);

-- 选课表索引
CREATE INDEX IF NOT EXISTS idx_enrollment_course ON enrollments (course_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_student ON enrollments (student_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_status ON enrollments (status);