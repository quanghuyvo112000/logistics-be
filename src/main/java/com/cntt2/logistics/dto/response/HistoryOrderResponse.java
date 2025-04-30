package com.cntt2.logistics.dto.response;

import com.cntt2.logistics.entity.OrderStatus;
import com.cntt2.logistics.entity.OrderNote;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HistoryOrderResponse {

    String warehouseName;
    String status;
    String timestamp;
}
