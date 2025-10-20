package com.zjgsu.cyd.course.controller;



import com.zjgsu.cyd.course.Response.Result;
import com.zjgsu.cyd.course.model.Student;
import com.zjgsu.cyd.course.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Validated
public class StudentController {
    @Autowired
    private StudentService studentService;

    // 1. 创建学生（POST /api/students）
    @PostMapping
    public ResponseEntity<Result<Student>> createStudent(@Valid @RequestBody Student student) {
        try {
            Student createdStudent = studentService.createStudent(student);
            return new ResponseEntity<>(Result.created(createdStudent), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Result.error(400, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // 2. 查询所有学生（GET /api/students）
    @GetMapping
    public Result<List<Student>> getAllStudents() {
        List<Student> students = studentService.findAllStudents();
        return Result.success(students);
    }

    // 3. 按ID查询学生（GET /api/students/{id}）
    @GetMapping("/{id}")
    public Result<Student> getStudentById(@PathVariable String id) {
        return studentService.findStudentById(id)
                .map(Result::success)
                .orElse(Result.error(404, "Student not found with id: " + id));
    }

    // 4. 更新学生信息（PUT /api/students/{id}）
    @PutMapping("/{id}")
    public ResponseEntity<Result<Student>> updateStudent(
            @PathVariable String id,
            @Valid @RequestBody Student student
    ) {
        try {
            Student updatedStudent = studentService.updateStudent(id, student);
            return new ResponseEntity<>(Result.success(updatedStudent), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Result.error(404, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // 5. 删除学生（DELETE /api/students/{id}）
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteStudent(@PathVariable String id) {
        try {
            studentService.deleteStudent(id);
            // 按文档要求，删除成功返回204 No Content（无响应体）
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Result.error(404, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}