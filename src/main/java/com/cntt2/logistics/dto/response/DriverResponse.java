package com.cntt2.logistics.dto.response;

import com.cntt2.logistics.entity.Role;
import com.cntt2.logistics.entity.VehicleType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverResponse {
    String fullName;
    String phone;
    String email;
    LocalDate birthday;
    String province;
    String district;
    String ward;
    Role role;
    VehicleType vehicleType;
    String vehiclePlate;
}
