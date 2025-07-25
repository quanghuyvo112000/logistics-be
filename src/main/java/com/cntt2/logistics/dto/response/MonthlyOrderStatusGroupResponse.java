package com.cntt2.logistics.dto.response;

import com.cntt2.logistics.entity.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MonthlyOrderStatusGroupResponse {
    List<MonthlyOrderStatusResponse> created;
    List<MonthlyOrderStatusResponse> deliveredSuccessfully;
}
