package com.cntt2.logistics.dto.request;

import com.cntt2.logistics.entity.VehicleType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@EqualsAndHashCode(callSuper = true)
public class DriverRequest extends UserRequest {
    String warehouseId;
    VehicleType vehicleType;
    String vehiclePlate;
}
