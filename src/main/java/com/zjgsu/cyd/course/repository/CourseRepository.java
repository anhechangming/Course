package com.zjgsu.cyd.course.repository;

import com.zjgsu.cyd.course.model.Course;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CourseRepository {
    // 内存存储：key=课程ID，value=课程对象
    private final Map<String, Course> courses = new ConcurrentHashMap<>();

    // 1. 查询所有课程
    public List<Course> findAll() {
        return new ArrayList<>(courses.values());
    }

    // 2. 按ID查询课程
    public Optional<Course> findById(String id) {
        return Optional.ofNullable(courses.get(id));
    }

    // 3. 保存课程（新增/更新通用）
    public Course save(Course course) {
        courses.put(course.getId(), course);
        return course;
    }

    // 4. 按ID删除课程
    public void deleteById(String id) {
        courses.remove(id);
    }

    // 5. 检查课程代码是否已存在（避免重复）
    public boolean existsByCode(String code) {
        return courses.values().stream()
                .anyMatch(course -> course.getCode().equals(code));
    }
}