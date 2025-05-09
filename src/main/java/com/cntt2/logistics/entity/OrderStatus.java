package com.cntt2.logistics.entity;

public enum OrderStatus {
    CREATED, // Đơn hàng vừa được tạo
    RECEIVED_AT_SOURCE, // Đã đến kho nguồn
    LEFT_SOURCE, // Đã rời khỏi kho nguồn
    AT_INTERMEDIATE, // Đang ở kho trung gian
    LEFT_INTERMEDIATE, // Đã rời khỏi kho trung gian
    AT_DESTINATION, // Đã đến kho đích
    OUT_FOR_DELIVERY, // Đang được giao đến người nhận
    DELIVERED_SUCCESSFULLY, // Giao hàng thành công
    DELIVERY_FAILED, // Giao hàng thất bại
    ASSIGNED_TO_SHIPPER, // Đã được gán cho shipper để lấy hàng
    PICKED_UP_SUCCESSFULLY // Lấy hàng thành công từ kho nguồn
}
