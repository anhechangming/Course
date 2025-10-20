package com.zjgsu.cyd.course.controller;


import com.zjgsu.cyd.course.Response.Result;
import com.zjgsu.cyd.course.model.Enrollment;
import com.zjgsu.cyd.course.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.zjgsu.cyd.course.common.GlobalExceptionHandler;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@Validated
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;

    // 1. 学生选课（POST /api/enrollments）
    @PostMapping
    public ResponseEntity<Result<Enrollment>> enrollCourse(@Valid @RequestBody Enrollment enrollment) {
        try {
            Enrollment savedEnrollment = enrollmentService.enrollCourse(enrollment);
            return new ResponseEntity<>(Result.created(savedEnrollment), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // 重复选课/容量已满返回400
            return new ResponseEntity<>(Result.error(400, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // 课程/学生不存在返回404
            return new ResponseEntity<>(Result.error(404, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // 2. 学生退课（DELETE /api/enrollments/{id}）
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> dropCourse(@PathVariable String id) {
        try {
            enrollmentService.dropCourse(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            // 选课记录不存在返回404
            return new ResponseEntity<>(Result.error(404, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // 3. 查询所有选课记录（GET /api/enrollments）
    @GetMapping
    public Result<List<Enrollment>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.findAllEnrollments();
        return Result.success(enrollments);
    }

    // 4. 按课程ID查询选课记录（GET /api/enrollments/course/{courseId}）
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Result<List<Enrollment>>> getEnrollmentsByCourseId(@PathVariable String courseId) {
        try {
            List<Enrollment> enrollments = enrollmentService.findEnrollmentsByCourseId(courseId);
            return new ResponseEntity<>(Result.success(enrollments), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Result.error(404, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // 5. 按学生学号查询选课记录（GET /api/enrollments/student/{studentId}）
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Result<List<Enrollment>>> getEnrollmentsByStudentId(@PathVariable String studentId) {
        try {
            List<Enrollment> enrollments = enrollmentService.findEnrollmentsByStudentId(studentId);
            return new ResponseEntity<>(Result.success(enrollments), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Result.error(404, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}