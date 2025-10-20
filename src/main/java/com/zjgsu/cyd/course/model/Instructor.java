package com.zjgsu.cyd.course.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Instructor {
    // 讲师ID（如T001），非空
    @NotBlank(message = "Instructor ID cannot be blank")
    private String id;

    // 讲师姓名，非空
    @NotBlank(message = "Instructor name cannot be blank")
    private String name;

    // 讲师邮箱，需符合标准格式
    @NotBlank(message = "Instructor email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;
}