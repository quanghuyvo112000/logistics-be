package com.cntt2.logistics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "history_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HistoryOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties("historyOrders")
    Order order;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    @JsonIgnoreProperties({"manager", "drivers"})
    WarehouseLocations warehouse;

    @Column(nullable = false, length = 20)
    String trackingCode; // Mã vận đơn

    @Column(nullable = false)
    LocalDateTime timestamp = LocalDateTime.now();

    @Column
    String expectedDeliveryTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatus status; // Ghi lại trạng thái đơn hàng tại thời điểm đó (e.g. AT_WAREHOUSE, LEFT_WAREHOUSE, DELIVERED...)

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }
}
