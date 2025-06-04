package com.cntt2.logistics.dto.response;

import com.cntt2.logistics.entity.IncomeType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncomeResponse {

    Double amountShipper;
    Double amountWarehouse;
    IncomeType type;
    LocalDateTime createdAt;
}
