package com.cntt2.logistics.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class SearchWarehouseLocationResponse {
    String id;
    String warehouseName;
}
