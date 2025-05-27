package com.cntt2.logistics.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderConfirmPickupRequest {
    String trackingCode;
    String paymentStatus;
    MultipartFile pickupImage;
}
