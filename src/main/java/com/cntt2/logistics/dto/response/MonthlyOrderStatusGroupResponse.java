package com.cntt2.logistics.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonthlyOrderStatusGroupResponse {
    List<MonthlyOrderStatusResponse> created;
    List<MonthlyOrderStatusResponse> deliveredSuccessfully;
}

