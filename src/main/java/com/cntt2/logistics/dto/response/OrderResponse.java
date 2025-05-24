package com.cntt2.logistics.dto.response;

import com.cntt2.logistics.entity.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

    String trackingCode;

    String customerName;
    String sourceWarehouseName;
    String destinationWarehouseName;

    String senderName;
    String senderPhone;
    String senderAddress;

    String receiverName;
    String receiverPhone;
    String receiverAddress;

    Double weight;
    Double orderPrice;
    Double shippingFee;

    String pickupImage;
    String deliveryImage;
    Boolean isPickupDriverNull;
    Boolean isDeliveryDriverNull;

    String expectedDeliveryTime;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}