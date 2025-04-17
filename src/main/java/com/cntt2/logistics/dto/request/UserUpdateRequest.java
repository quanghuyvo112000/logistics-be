package com.cntt2.logistics.dto.request;

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
public class UserUpdateRequest {
    String fullName;
    LocalDate birthday;
    String phone;
    String province;
    String district;
    String ward;
    String address;
    LocalDateTime updatedAt;
}
