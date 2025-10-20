package com.zjgsu.cyd.course.repository;



import com.zjgsu.cyd.course.model.Enrollment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EnrollmentRepository {
    // 内存存储：key=选课记录ID（UUID），value=选课对象
    private final Map<String, Enrollment> enrollments = new ConcurrentHashMap<>();

    // 1. 保存选课记录（新增/更新通用）
    public Enrollment save(Enrollment enrollment) {
        enrollments.put(enrollment.getId(), enrollment);
        return enrollment;
    }

    // 2. 查询所有选课记录
    public List<Enrollment> findAll() {
        return new ArrayList<>(enrollments.values());
    }

    // 3. 按ID查询选课记录（用于退课）
    public Optional<Enrollment> findById(String id) {
        return Optional.ofNullable(enrollments.get(id));
    }

    // 4. 按课程ID查询选课记录（用于统计课程选课人数、验证容量）
    public List<Enrollment> findByCourseId(String courseId) {
        return enrollments.values().stream()
                .filter(enrollment -> enrollment.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }

    // 5. 按学生学号查询选课记录（用于学生退课、删除学生校验）
    public List<Enrollment> findByStudentId(String studentId) {
        return enrollments.values().stream()
                .filter(enrollment -> enrollment.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    // 6. 校验重复选课（同一学生是否已选同一课程）
    public boolean existsByCourseIdAndStudentId(String courseId, String studentId) {
        return enrollments.values().stream()
                .anyMatch(enrollment -> enrollment.getCourseId().equals(courseId)
                        && enrollment.getStudentId().equals(studentId));
    }

    // 7. 按ID删除选课记录（退课）
    public void deleteById(String id) {
        enrollments.remove(id);
    }

    // 8. 校验学生是否有选课记录（供学生删除时使用）
    public boolean existsByStudentId(String studentId) {
        return !findByStudentId(studentId).isEmpty();
    }
}