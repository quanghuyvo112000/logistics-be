package com.cntt2.logistics.dto.request;

import com.cntt2.logistics.entity.OrderNote;
import com.cntt2.logistics.entity.OrderStatus;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HistoryOrderRequest {
    String trackingCode;
    String warehouseId;
    String status;
}
