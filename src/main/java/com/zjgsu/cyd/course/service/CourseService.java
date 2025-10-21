package com.zjgsu.cyd.course.service;

import com.zjgsu.cyd.course.DTO.PageQueryDTO;
import com.zjgsu.cyd.course.model.Course;
import com.zjgsu.cyd.course.repository.CourseRepository;
import org.springdoc.core.converters.models.Pageable;
import org.springdoc.core.converters.models.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    // 检查课程时间冲突
    private void checkTimeConflict(Course course, String excludeId) {
        // 获取所有课程
        List<Course> allCourses = courseRepository.findAll();

        // 检查是否有时间冲突
        boolean hasConflict = allCourses.stream()
                .filter(existingCourse -> excludeId == null || !existingCourse.getId().equals(excludeId))
                .anyMatch(existingCourse ->
                        existingCourse.getInstructor().equals(course.getInstructor()) &&
                                existingCourse.getSchedule().equals(course.getSchedule())
                );

        if (hasConflict) {
            throw new IllegalArgumentException("Time conflict detected: Instructor '" +
                    course.getInstructor() + "' already has a course at '" +
                    course.getSchedule() + "'");
        }
    }
    // 分页查询课程（从内存数据查询，支持分页和排序）
    public List<Course> getCoursesByPage(PageQueryDTO pageQuery) {
        // 1. 获取所有课程数据
        List<Course> allCourses = this.findAllCourses(); // 复用已有方法获取排序后的数据

        // 2. 处理分页参数
        int pageNum = Math.max(pageQuery.getPageNum() - 1, 0); // 保证页码非负
        int pageSize = pageQuery.getPageSize();

        // 3. 计算分页索引
        int totalElements = allCourses.size();
        int startIndex = pageNum * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalElements);

        // 4. 如果起始索引超出范围，返回空列表
        if (startIndex >= totalElements) {
            return new ArrayList<>();
        }

        // 5. 返回当前页的数据列表
        return new ArrayList<>(allCourses.subList(startIndex, endIndex));
    }



    // 1. 查询所有课程
    public List<Course> findAllCourses() {
        List<Course> courses = courseRepository.findAll();
        // 按 course.code 升序排序
        Collections.sort(courses, Comparator.comparing(Course::getCode));
        return courses;
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
        // 检查时间冲突
        checkTimeConflict(course, null);
        // 自动生成课程ID（符合“系统生成唯一标识”要求）
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
        // 检查时间冲突（排除自身）
        checkTimeConflict(updatedCourse, id);

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