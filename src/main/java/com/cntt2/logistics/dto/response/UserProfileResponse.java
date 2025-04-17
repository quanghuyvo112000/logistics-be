package com.cntt2.logistics.dto.response;

import com.cntt2.logistics.entity.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileResponse {
    String fullName;
    String email;
    LocalDate birthday;
    String phone;
    String province;
    String district;
    String ward;
    String address;
    Role role;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
