package com.cntt2.logistics.dto.response;

import com.cntt2.logistics.entity.DriverShift;
import com.cntt2.logistics.entity.ScheduleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverWorkScheduleResponse {
    String id;
    String nameDriver;
    LocalDate workDate;
    String startTime;
    String endTime;
    DriverShift shift;
    ScheduleStatus status;
    String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
