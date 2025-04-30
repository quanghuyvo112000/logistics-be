package com.cntt2.logistics.entity;

public enum OrderStatus {
    CREATED, // Đơn hàng vừa được tạo
    RECEIVED_AT_SOURCE,
    LEFT_SOURCE,
    AT_INTERMEDIATE,
    LEFT_INTERMEDIATE,
    AT_DESTINATION,
    OUT_FOR_DELIVERY,
    DELIVERED_SUCCESSFULLY,
    DELIVERY_FAILED,
    ASSIGNED_TO_SHIPPER
}
