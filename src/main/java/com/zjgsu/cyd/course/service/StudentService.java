package com.zjgsu.cyd.course.service;


import com.zjgsu.cyd.course.model.Student;
import com.zjgsu.cyd.course.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    // 依赖选课Service，用于校验学生是否有选课记录
    @Autowired
    private EnrollmentService enrollmentService;

    // 1. 创建学生（自动生成ID和时间戳，校验学号唯一性）
    public Student createStudent(Student student) {
        // 校验学号是否已存在
        if (studentRepository.existsByStudentId(student.getStudentId())) {
            throw new IllegalArgumentException("Student ID (studentId) already exists: " + student.getStudentId());
        }
        // 自动初始化ID和创建时间
        student.init();
        return studentRepository.save(student);
    }

    // 2. 查询所有学生
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    // 3. 按ID（UUID）查询学生
    public Optional<Student> findStudentById(String id) {
        return studentRepository.findById(id);
    }

    // 4. 按学号（studentId）查询学生（供选课模块使用）
    public Optional<Student> findStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    // 5. 更新学生信息（禁止修改ID和创建时间，校验学号唯一性）
    public Student updateStudent(String id, Student updatedStudent) {
        // 校验学生是否存在
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        // 若更新学号，需校验新学号是否已被其他学生使用
        if (!existingStudent.getStudentId().equals(updatedStudent.getStudentId())
                && studentRepository.existsByStudentId(updatedStudent.getStudentId())) {
            throw new IllegalArgumentException("Updated studentId already exists: " + updatedStudent.getStudentId());
        }

        // 保留系统生成的字段（ID、创建时间），更新其他可修改字段
        updatedStudent.setId(existingStudent.getId());
        updatedStudent.setCreatedAt(existingStudent.getCreatedAt());
        return studentRepository.save(updatedStudent);
    }

    // 6. 删除学生（校验是否有选课记录，有则禁止删除）
    public void deleteStudent(String id) {
        // 校验学生是否存在
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        // 校验学生是否有选课记录（调用选课Service）
        if (enrollmentService.existsByStudentId(student.getStudentId())) {
            throw new IllegalArgumentException("Cannot delete student: Student has active enrollments");
        }

        // 无选课记录，执行删除
        studentRepository.deleteById(id);
    }
}