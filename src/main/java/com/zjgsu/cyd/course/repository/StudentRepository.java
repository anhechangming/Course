package com.zjgsu.cyd.course.repository;

import com.zjgsu.cyd.course.model.Student;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class StudentRepository {
    // 内存存储：key=学生ID（UUID），value=学生对象
    private final Map<String, Student> students = new ConcurrentHashMap<>();

    // 1. 保存学生（新增/更新通用）
    public Student save(Student student) {
        students.put(student.getId(), student);
        return student;
    }

    // 2. 查询所有学生
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }

    // 3. 按ID（UUID）查询学生
    public Optional<Student> findById(String id) {
        return Optional.ofNullable(students.get(id));
    }

    // 4. 按学号（studentId）查询学生（用于唯一性校验、选课学生验证）
    public Optional<Student> findByStudentId(String studentId) {
        return students.values().stream()
                .filter(student -> student.getStudentId().equals(studentId))
                .findFirst();
    }

    // 5. 按ID删除学生
    public void deleteById(String id) {
        students.remove(id);
    }

    // 6. 校验学号是否已存在（创建/更新学生时使用）
    public boolean existsByStudentId(String studentId) {
        return students.values().stream()
                .anyMatch(student -> student.getStudentId().equals(studentId));
    }
}