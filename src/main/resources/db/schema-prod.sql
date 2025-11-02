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
