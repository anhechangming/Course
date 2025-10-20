package com.zjgsu.cyd.course.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    // 课程唯一ID（系统自动生成UUID）
    private String id;

    // 课程代码（如CS101），非空
    @NotBlank(message = "Course code cannot be blank")
    private String code;

    // 课程名称（如计算机科学导论），非空
    @NotBlank(message = "Course title cannot be blank")
    private String title;

    // 讲师信息（嵌套验证）
    @Valid
    private Instructor instructor;

    // 排课信息（嵌套验证）
    @Valid
    private ScheduleSlot schedule;

    // 课程容量（最大选课人数），需为正数
    @Positive(message = "Course capacity must be positive")
    private Integer capacity;

    // 已选人数（初始为0，选课时自动增加）
    private Integer enrolled = 0;

    // 创建课程时自动生成ID
    public void generateId() {
        this.id = UUID.randomUUID().toString().replace("-", "");
    }
}