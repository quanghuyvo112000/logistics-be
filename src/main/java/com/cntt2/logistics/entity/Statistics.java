package com.cntt2.logistics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"historyOrders", "pickupImage", "deliveryImage"})
    Order order;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    @JsonIgnoreProperties({"user", "warehouse", "schedules"})
    Driver driver;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    @JsonIgnoreProperties({"drivers", "manager"})
    WarehouseLocations warehouse;

    @Column(nullable = false)
    Double amountShipper;

    @Column(nullable = false)
    Double amountWarehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    IncomeType type; // Enum: SHIPPING_FEE, COMMISSION, OTHER

    @Column(nullable = false)
    LocalDateTime createdAt = LocalDateTime.now();
}
