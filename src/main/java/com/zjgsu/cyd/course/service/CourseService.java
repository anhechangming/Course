package com.zjgsu.cyd.course.service;

import com.zjgsu.cyd.course.model.Course;
import com.zjgsu.cyd.course.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    // 1. 查询所有课程
    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    // 2. 按ID查询课程（返回Optional，便于控制器处理不存在场景）
    public Optional<Course> findCourseById(String id) {
        return courseRepository.findById(id);
    }

    // 3. 创建课程（自动生成ID，校验课程代码唯一性）
    public Course createCourse(Course course) {
        // 校验课程代码是否已存在（推导自学生学号唯一规则）
        boolean codeExists = courseRepository.findAll().stream()
                .anyMatch(existingCourse -> existingCourse.getCode().equals(course.getCode()));
        if (codeExists) {
            // 抛出非法参数异常，说明异常原因
            throw new IllegalArgumentException("Course code already exists: " + course.getCode());
        }
        // 自动生成课程ID（符合文档“系统生成唯一标识”要求）
        course.setId(UUID.randomUUID().toString().replace("-", ""));
        return courseRepository.save(course);
    }

    // 更新课程：禁止修改课程代码
    public Course updateCourse(String id, Course updatedCourse) {
        // 先查询课程是否存在（不存在抛RuntimeException，后续返回404）
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        // 校验是否修改了课程代码
        if (!existingCourse.getCode().equals(updatedCourse.getCode())) {
            throw new IllegalArgumentException("Course code cannot be modified (original: " + existingCourse.getCode() + ")");
        }

        // 校验容量是否为正数（补充文档未明确的逻辑校验）
        if (updatedCourse.getCapacity() <= 0) {
            throw new IllegalArgumentException("Course capacity must be positive: " + updatedCourse.getCapacity());
        }

        // 更新合法字段并保存
        existingCourse.setTitle(updatedCourse.getTitle());
        existingCourse.setInstructor(updatedCourse.getInstructor());
        existingCourse.setSchedule(updatedCourse.getSchedule());
        existingCourse.setCapacity(updatedCourse.getCapacity());
        return courseRepository.save(existingCourse);
    }

    // 5. 删除课程（需先确认课程存在）
    public void deleteCourse(String id) {
        // 检查课程是否存在
        if (!courseRepository.findById(id).isPresent()) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        // 删除课程
        courseRepository.deleteById(id);
    }
}