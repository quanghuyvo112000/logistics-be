package com.cntt2.logistics.dto.request;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String fullName;
    LocalDate birthday;
    String email;
    String phone;
    String password;
    String province;
    String district;
    String ward;
    String address;
}

