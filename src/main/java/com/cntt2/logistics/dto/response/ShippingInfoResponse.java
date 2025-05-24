package com.cntt2.logistics.dto.response;

import com.cntt2.logistics.entity.DriverShift;
import com.cntt2.logistics.entity.ScheduleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingInfoResponse {
    double shippingFee;
    String estimatedDeliveryTime;
}
