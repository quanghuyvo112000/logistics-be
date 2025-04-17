package com.cntt2.logistics.dto.request;

import com.cntt2.logistics.entity.VehicleType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDriverRequest {
    String fullName;
    String phone;
    String gmail;
    LocalDate birthday;
    String password;
    VehicleType vehicleType;
    String vehiclePlate;
}

