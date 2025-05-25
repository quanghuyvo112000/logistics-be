package com.cntt2.logistics.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ShippingInfoRequest {
    String fromWarehouseId;
    String toWarehouseId;
}
