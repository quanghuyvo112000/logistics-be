package com.cntt2.logistics.repository;

import com.cntt2.logistics.entity.Order;
import com.cntt2.logistics.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findBySourceWarehouseId(String sourceWarehouse);
    List<Order> findByDestinationWarehouseId(String destinationWarehouse);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByPickupDriverId(String pickupDriverId);
    List<Order> findByDeliveryDriverId(String deliveryDriverId);

    Order findByTrackingCode(String trackingCode);

    List<Order> findByStatus(OrderStatus status);
}
