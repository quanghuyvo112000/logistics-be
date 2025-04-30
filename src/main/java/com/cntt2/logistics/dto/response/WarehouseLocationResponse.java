package com.cntt2.logistics.dto.response;

import com.cntt2.logistics.entity.Role;
import com.cntt2.logistics.entity.VehicleType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class  WarehouseLocationResponse {
    String id;
    String name;
    String phone;
    String province;
    String district;
    String address;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    ManagerResponse manager;
    List<DriverResponse> drivers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ManagerResponse {
        String fullName;
        String email;
        LocalDate birthday;
        String phone;
        String province;
        String district;
        String ward;
        String address;
        Role role;
        String createdBy;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DriverResponse {
        String fullName;
        String email;
        String phone;
        LocalDate birthday;
        Role role;
        String province;
        String district;
        String ward;
        String address;
        VehicleType vehicleType;
        String vehiclePlate;
        String createdBy;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }
}
