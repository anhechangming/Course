package com.zjgsu.cyd.course.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ScheduleSlot {
    // 星期（如MONDAY、TUESDAY，需符合枚举规范）
    @NotBlank(message = "Day of week cannot be blank")
    private String dayOfWeek;

    // 开始时间（如08:00）
    @NotBlank(message = "Start time cannot be blank")
    private String startTime;

    // 结束时间（如10:00）
    @NotBlank(message = "End time cannot be blank")
    private String endTime;

    // 预期出勤人数，需为正数
    @Positive(message = "Expected attendance must be positive")
    private Integer expectedAttendance;
}