package com.cntt2.logistics.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseAmountAdminResponse {
    Integer warehouseName;
    Double totalAmount;
}
