package com.zjgsu.cyd.course.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    // 系统自动生成UUID，无需请求体提供
    private String id;

    // 学号（如S2024001），全局唯一，必填
    @NotBlank(message = "Student ID (studentId) cannot be blank")
    private String studentId;

    // 学生姓名，必填
    @NotBlank(message = "Student name cannot be blank")
    private String name;

    // 专业名称，必填
    @NotBlank(message = "Student major cannot be blank")
    private String major;

    // 入学年份（如2024），必填
    @NotNull(message = "Student grade cannot be null")
    private Integer grade;

    // 邮箱，需符合标准格式，必填
    @NotBlank(message = "Student email cannot be blank")
    @Email(message = "Invalid email format (e.g. xxx@xxx.com)")
    private String email;

    // 系统自动生成创建时间戳，无需请求体提供
    private LocalDateTime createdAt;

    // 创建学生时自动生成ID和创建时间
    public void init() {
        this.id = UUID.randomUUID().toString().replace("-", "");
        this.createdAt = LocalDateTime.now();
    }
}