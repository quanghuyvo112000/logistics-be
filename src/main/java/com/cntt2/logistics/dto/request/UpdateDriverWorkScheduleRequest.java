package com.cntt2.logistics.dto.request;

import com.cntt2.logistics.entity.ScheduleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDriverWorkScheduleRequest {
    String scheduleId;
    ScheduleStatus status;
}
