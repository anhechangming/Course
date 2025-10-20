package com.zjgsu.cyd.course.model;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    // 选课记录唯一ID（系统自动生成UUID）
    private String id;

    // 课程ID（关联Course的id字段），必填
    @NotBlank(message = "Course ID cannot be blank")
    private String courseId;

    // 学生学号（关联Student的studentId字段），必填
    @NotBlank(message = "Student ID (studentId) cannot be blank")
    private String studentId;

    // 创建选课记录时自动生成ID
    public void init() {
        this.id = UUID.randomUUID().toString().replace("-", "");
    }
}