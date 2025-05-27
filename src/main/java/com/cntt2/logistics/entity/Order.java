package com.cntt2.logistics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true, length = 20)
    String trackingCode; // Mã vận đơn

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"password", "driver", "warehouse", "role"})
    User customer;

    @ManyToOne
    @JoinColumn(name = "source_warehouse_id", nullable = false)
    @JsonIgnoreProperties({"drivers", "manager"})
    WarehouseLocations sourceWarehouse;

    @ManyToOne
    @JoinColumn(name = "destination_warehouse_id", nullable = false)
    @JsonIgnoreProperties({"drivers", "manager"})
    WarehouseLocations destinationWarehouse;

    @ManyToOne
    @JoinColumn(name = "pickup_driver_id")
    @JsonIgnoreProperties({"user", "warehouse", "schedules"})
    Driver pickupDriver;

    @ManyToOne
    @JoinColumn(name = "delivery_driver_id")
    @JsonIgnoreProperties({"user", "warehouse", "schedules"})
    Driver deliveryDriver;

    @Column(nullable = false)
    String senderName;

    @Column(nullable = false)
    String senderPhone;

    @Column(nullable = false)
    String senderAddress;

    @Column(nullable = false)
    String receiverName;

    @Column(nullable = false)
    String receiverPhone;

    @Column(nullable = false)
    String receiverAddress;

    @Column(nullable = false)
    String expectedDeliveryTime;

    @Column(nullable = false)
    Double weight;

    @Column(nullable = false)
    Double orderPrice;

    @Column(nullable = false)
    Double shippingFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentStatus paymentStatus;

    @Lob
    @Column(name = "pickupImage", columnDefinition = "LONGBLOB")
    byte[] pickupImage;

    @Lob
    @Column(name = "deliveryImage", columnDefinition = "LONGBLOB")
    byte[] deliveryImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatus status; // Enum gồm các trạng thái: CREATED, RECEIVED, IN_TRANSIT, DELIVERED, FAILED, etc.

    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("order")
    List<HistoryOrder> historyOrders;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
