package com.cntt2.logistics.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderByManagerResponse {
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

    String expectedDeliveryTime;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    Boolean isSourceWarehouse;
    Boolean isPickupDriverNull;
    Boolean isDeliveryDriverNull;
    String warehouseManagerRole;
}