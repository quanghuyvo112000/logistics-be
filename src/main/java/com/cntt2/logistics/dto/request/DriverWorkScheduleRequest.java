package com.cntt2.logistics.dto.request;

import com.cntt2.logistics.entity.DriverShift;
import com.cntt2.logistics.entity.ScheduleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverWorkScheduleRequest {
    LocalDate workDate;
    String startTime;
    String endTime;
    DriverShift shift;
    String note;
}
