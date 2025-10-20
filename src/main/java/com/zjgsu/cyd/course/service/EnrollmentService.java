package com.zjgsu.cyd.course.service;


import com.zjgsu.cyd.course.model.Course;
import com.zjgsu.cyd.course.model.Enrollment;
import com.zjgsu.cyd.course.model.Student;
import com.zjgsu.cyd.course.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // 依赖课程Service，用于校验课程存在性、更新已选人数
    @Autowired
    private CourseService courseService;

    // 依赖学生Service，用于校验学生存在性
    @Autowired
    private StudentService studentService;

    // 1. 学生选课（核心业务逻辑：校验+级联更新）
    public Enrollment enrollCourse(Enrollment enrollment) {
        String courseId = enrollment.getCourseId();
        String studentId = enrollment.getStudentId();

        // 校验1：课程是否存在
        Course course = courseService.findCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        // 校验2：学生是否存在
        Student student = studentService.findStudentByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with studentId: " + studentId));

        // 校验3：是否重复选课
        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new IllegalArgumentException("Duplicate enrollment: Student " + studentId + " already enrolled in Course " + courseId);
        }

        // 校验4：课程容量是否已满（已选人数 >= 容量则禁止选课）
        List<Enrollment> courseEnrollments = enrollmentRepository.findByCourseId(courseId);
        if (courseEnrollments.size() >= course.getCapacity()) {
            throw new IllegalArgumentException("Course capacity exceeded: Current enrolled " + courseEnrollments.size() + ", Capacity " + course.getCapacity());
        }

        // 执行选课：生成记录ID + 保存
        enrollment.init();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // 级联更新：课程已选人数（enrolled字段+1）
        course.setEnrolled(course.getEnrolled() + 1);
        courseService.updateCourse(courseId, course); // 调用课程更新方法

        return savedEnrollment;
    }

    // 2. 学生退课（级联更新课程已选人数）
    public void dropCourse(String enrollmentId) {
        // 校验选课记录是否存在
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));

        // 执行退课：删除选课记录
        enrollmentRepository.deleteById(enrollmentId);

        // 级联更新：课程已选人数（enrolled字段-1）
        Course course = courseService.findCourseById(enrollment.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + enrollment.getCourseId()));
        course.setEnrolled(Math.max(0, course.getEnrolled() - 1)); // 避免负数
        courseService.updateCourse(course.getId(), course);
    }

    // 3. 查询所有选课记录
    public List<Enrollment> findAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    // 4. 按课程ID查询选课记录
    public List<Enrollment> findEnrollmentsByCourseId(String courseId) {
        // 校验课程是否存在（可选，增强容错）
        courseService.findCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        return enrollmentRepository.findByCourseId(courseId);
    }

    // 5. 按学生学号查询选课记录
    public List<Enrollment> findEnrollmentsByStudentId(String studentId) {
        // 校验学生是否存在（可选，增强容错）
        studentService.findStudentByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with studentId: " + studentId));
        return enrollmentRepository.findByStudentId(studentId);
    }

    // 6. 校验学生是否有选课记录（供学生Service删除学生时使用）
    public boolean existsByStudentId(String studentId) {
        return enrollmentRepository.existsByStudentId(studentId);
    }
}