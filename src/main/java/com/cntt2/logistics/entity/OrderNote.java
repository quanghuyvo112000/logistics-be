package com.cntt2.logistics.entity;

public enum OrderNote {
    SUCCESS("Giao hàng thành công"),
    DELIVERY_FAILED("Giao không thành công"),
    RECIPIENT_NOT_FOUND("Người nhận không có nhà"),
    DELAYED("Giao hàng bị trì hoãn"),
    PENDING("Đang chờ xử lý"),
    CANCELLED("Đơn hàng bị hủy");

    private final String description;

    OrderNote(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}