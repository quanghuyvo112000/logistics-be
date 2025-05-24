package com.cntt2.logistics.dto.response;

import com.cntt2.logistics.entity.OrderStatus;
import com.cntt2.logistics.entity.OrderNote;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HistoryOrderResponse {
    String trackingCode;
    LocalDateTime expectedDeliveryTime;
    List<HistoryItem> histories;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryItem {
        String status;
        java.time.LocalDateTime timestamp;
    }
}
